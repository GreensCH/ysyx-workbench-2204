#include "include.h"
#include "sdb.h"

/* We use the POSIX regex functions to process regular expressions.
 * Type 'man regex' for more information about POSIX regex functions.
 */
#include <regex.h>

// #define TEST_EXPR //开启调试信息

enum {

  /* 优先级从高到低 */
  TK_NOTYPE = 256,
  TK_MINUS,
  TK_DERE,//指针解引用
  TK_EQ,TK_NEQ,
  TK_AND,TK_XOR,TK_OR,//逻辑运算
  TK_REG,
  TK_HEX,
  TK_NUM,
};

static struct rule {
  const char *regex;
  int token_type;
} rules[] = {
  {"\\(", '('},         // left bracket 40
  {"\\)", ')'},         // right bracket 41
  {"\\*", '*'},         // mul 42
  {"\\+", '+'},         // plus 43
  {"\\-", '-'},         // sub 45
  {"\\/", '/'},         // div 47
  {" +", TK_NOTYPE},    // spaces 
  {"==", TK_EQ},        // equal
  {"!=", TK_NEQ},       // not equal
  {"\\&\\&", TK_AND},       // not equal
  {"\\^",TK_XOR},       // not equal
  {"\\|\\|", TK_OR},       // not equal
  {"\\$[$0-9a-zA-Z]+", TK_REG},   // reg number
  {"0[xX][0-9a-fA-F]+", TK_HEX},   // hex number
  {"^[0-9]+", TK_NUM},   // dec number
  // {"[0-9]+", TK_DERE},   // pointer dereference
};

#define NR_REGEX ARRLEN(rules)

static regex_t re[NR_REGEX] = {};

/* Rules are used for many times.
 * Therefore we compile them only once before any usage.
 */
void init_regex() {
  int i;
  char error_msg[128];
  int ret;

  for (i = 0; i < NR_REGEX; i ++) {
    ret = regcomp(&re[i], rules[i].regex, REG_EXTENDED);
    if (ret != 0) {
      regerror(ret, &re[i], error_msg, 128);
      panic("regex compilation failed: %s\n%s", error_msg, rules[i].regex);
    }
  }
}

typedef struct token {
  int type;
  char str[32];
} Token;

static Token tokens[32] __attribute__((used)) = {};
static int nr_token __attribute__((used))  = 0;

static bool make_token(char *e) {
  int position = 0;
  int i;
  regmatch_t pmatch;

  nr_token = 0;

  while (e[position] != '\0') {
    /* Try all rules one by one. */
    for (i = 0; i < NR_REGEX; i ++) {
      if (regexec(&re[i], e + position, 1, &pmatch, 0) == 0 && pmatch.rm_so == 0) {
        char *substr_start = e + position;
        int substr_len = pmatch.rm_eo;
#ifdef TEST_EXPR
        Log("match rules[%d] = \"%s\" at position %d with len %d: %.*s",
            i, rules[i].regex, position, substr_len, substr_len, substr_start);
#endif
        position += substr_len;

        /* TODO: Now a new token is recognized with rules[i]. Add codes
         * to record the token in the array `tokens'. For certain types
         * of tokens, some extra actions should be performed.
         */
        Assert(nr_token<32, "*** Expression token array overflow! ***");//表达式数组溢出
        Assert(substr_len<32,"*** ERROR: Token too long! ***");//token字符串溢出
        switch (rules[i].token_type) {
          case(TK_NUM):
          case(TK_HEX):
          case(TK_REG):
            // Clone string
            for(int p=0; p<substr_len; p++)
              tokens[nr_token].str[p]=substr_start[p];
            tokens[nr_token].str[substr_len]='\0';
            // Transfer type
            tokens[nr_token].type=rules[i].token_type;
            // Transfer status
            nr_token+=1;
            break;
          case(TK_EQ):
          case(TK_NEQ):
          case(TK_AND):
          case(TK_XOR):
          case(TK_OR):
          case('-'):
          case('+'):
          case('*'):
          case('/'):
          case('('):
          case(')'):
            // Transfer type
            tokens[nr_token].type = rules[i].token_type;
            nr_token += 1;
            break;
          default: break;//TODO();
        }

        break;
      }
    }
    if (i == NR_REGEX) {
      Log("no match at position %d\n%s\n%*.s^\n", position, e, position, "");
      return false;
    }
  }
  return true;
}

/*
* 检查p,q处括号是否为一对
*/
bool check_parentheses(int p, int q){
  if(tokens[p].type != '(' || tokens[q].type != ')')
    return false;
  if(p>=q)
    return false;
  
  int64_t count = 0;
  for(int i = p +1; i < q; i++){//p+1到q可遍历范围为，去处最左端和最后端括号后的序列
    if(tokens[i].type == '(')
      count +=1;
    else if(tokens[i].type == ')')
      count -=1;
    if(count<0){
      return false;
    }
  }

  if(!count)
    return true;
  else
    return false;
}


int cal_pri_lut(int type){
  switch (type)
  {
  case TK_NUM: 
  case TK_HEX:
    return 100;
  case TK_OR:
    return 12;
  case TK_AND:
    return 11;
  case TK_XOR:
    return 9;
  case TK_EQ:
  case TK_NEQ:
    return 7;
  case '+'://第4优先级（加减逻辑法）
  case '-':
    return 4;
  case '*'://第3优先级（乘除法）
  case '/':
    return 3;
  case TK_MINUS://第2优先级（单操作数）
  case TK_DERE:
    return 2;
  case '('://第1优先级（括号）
  case ')'://第1优先级（括号）
    return 1;
  default:
    return 100;
  }
}

word_t eval(int p, int q, bool *success){
  if (p > q) {
    /* Bad expression */
    return 0;
  }
  else if (p == q) {
    word_t immediate = 0;
    if(tokens[p].type == TK_HEX)//16进制情况
      sscanf(tokens[p].str, "%lxu", &immediate);
    else if(tokens[p].type == TK_REG){//reg情况
      char buff[8];
      strncpy(buff, tokens[p].str + 1, 4);
      immediate = isa_reg_str2val(buff, success);
    }
    else//10进制情况
      sscanf(tokens[p].str, "%lu", &immediate);
    Assert(immediate!=-1, "*** ERROR: Token number overflow! ***");
    return immediate;
  }
  else if (check_parentheses(p, q) == true) {
    IFDEF(TEST_EXPR, Log("Check good p:%d,q:%d",p,q));
    return eval(p + 1, q - 1, success); 
  }
  else {
    int64_t val1;
    int64_t val2;
    int op = -1;
    int op_type = -1;
    int64_t count = 0;
    for(int i = p; i <= q; i++){
      //单符号逻辑
      if(tokens[i].type == '-' || tokens[i].type == '*'){// More Exprs Expected
        //单运算符在第0位的情况
        //去除前一位是数字位//和右括号位情况
        if(((i == 0 ) && (cal_pri_lut(TK_NUM) == cal_pri_lut(tokens[i + 1].type))) ||
        ((i == 0 ) && (tokens[i].type == '*')) ||
        (i > 0 && cal_pri_lut(TK_NUM) != cal_pri_lut(tokens[i-1].type) && 
        ')' != tokens[i-1].type)){
        /*cal_pri_lut('(')  != cal_pri_lut(tokens[i-1].type))){*/
            tokens[i].type = (tokens[i].type == '-') ? TK_MINUS :
                              (tokens[i].type == '*') ? TK_DERE : tokens[i].type;
        }
      }
      //其他意外排除(即两个符号连在一起)
      else if(!(cal_pri_lut(TK_NUM) == cal_pri_lut(tokens[i].type)) && 
      !(cal_pri_lut(TK_MINUS) == cal_pri_lut(tokens[i].type)) && 
      !(cal_pri_lut('(') == cal_pri_lut(tokens[i].type))){
        if(i == 0){
          IFDEF(TEST_EXPR, Log("*** ERROR Operator connection i:%d:%c ***",i , tokens[i].type));
          *success = false;
          return -1;
        }
        //去除前一位是数字或括号位情况（即前一位是数字或括号时正常）
        else if(i > 0 && 
        !(cal_pri_lut(TK_NUM) == cal_pri_lut(tokens[i-1].type)) && 
        !(cal_pri_lut('(') == cal_pri_lut(tokens[i-1].type))){
            IFDEF(TEST_EXPR, Log("*** ERROR Operator connection i:%d:%c%c ***",i , tokens[i-1].type, tokens[i].type));
            *success = false;
            return -1;
        }
        //else 正常
      } 
      //最外层的最低时记录op
      if(count == 0){
        //单操作数情况
        if(cal_pri_lut(TK_MINUS) == cal_pri_lut(tokens[i].type)){
          if(i == p || (i > p && //-- 分割为右处 -(-)
            cal_pri_lut(TK_MINUS) == cal_pri_lut(tokens[i-1].type)))
            op = i;
        }
        //其他情况
        else if(cal_pri_lut('(')    != cal_pri_lut(tokens[i].type) &&
                cal_pri_lut(TK_NUM) != cal_pri_lut(tokens[i].type)){
          //检测op是否存在低优先级,如果有则op不变,从而进一步递归
          if(op == -1 || cal_pri_lut(tokens[op].type) < cal_pri_lut(tokens[i].type))
            op = i;
        }
        else
          op = op;
      }
      //count 规则递进
      if(tokens[i].type == '(')
        count += 1;
      else if(tokens[i].type == ')')
        count -= 1;
    }
    //找不到主运算符
    if(op<0){
      IFDEF(TEST_EXPR, Log("*** ERROR Cannot get main operation position! ***"));
      *success = false;
      return -1;
    }
    //递归求值
    val1 = eval(p, op - 1, success);
    val2 = eval(op + 1, q, success);
    //类型转移
    op_type = tokens[op].type;
    IFDEF(TEST_EXPR, printf("主运算符:%c,val1:%ld,val2:%ld\n",op_type,val1,val2));
    *success = true;
    switch (op_type) {
      case '+':       return val1 + val2;
      case '-':       return val1 - val2;
      case '*':       return val1 * val2;
      case '/':       return val1 / val2;
      case TK_EQ    : return val1 == val2;
      case TK_NEQ   : return val1 != val2;
      case TK_AND   : return val1 && val2;
      case TK_XOR   : return val1 ^  val2;
      case TK_OR    : return val1 || val2;
      case TK_MINUS : return (-1) *  val2;
      case TK_DERE  : return (*((word_t *)val2));
      default:{
        IFDEF(TEST_EXPR, Log("*** ERROR: Operation %c not found ! ***",op_type));
        *success = false;
        return -1;
      }  
    }
  }
}



word_t expr(char *e, bool *success) {
  if (!make_token(e)) {
    *success = false;
    return 0;
  }
  return eval(0,nr_token-1,success);
}
