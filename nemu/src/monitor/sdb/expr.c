#include <isa.h>

/* We use the POSIX regex functions to process regular expressions.
 * Type 'man regex' for more information about POSIX regex functions.
 */
#include <regex.h>

enum {

  /* TODO: Add more token types */
  /* 优先级从高到低 */
  TK_NOTYPE = 256,
  TK_MINUS,
  TK_DERE,//指针解引用
  TK_EQ,TK_NEQ,
  TK_AND,TK_XOR,TK_OR,//逻辑运算
  TK_NUM,
  TK_HEX,
  TK_REG,
};

static struct rule {
  const char *regex;
  int token_type;
} rules[] = {

  /* TODO: Add more rules.
   * Pay attention to the precedence level of different rules.
   */

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
  {"\\$[0-9a-zA-Z]+", TK_REG},   // reg number
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

        Log("match rules[%d] = \"%s\" at position %d with len %d: %.*s",
            i, rules[i].regex, position, substr_len, substr_len, substr_start);

        position += substr_len;

        /* TODO: Now a new token is recognized with rules[i]. Add codes
         * to record the token in the array `tokens'. For certain types
         * of tokens, some extra actions should be performed.
         */
        Assert(nr_token<32, "*** Expression token array overflow! ***");//表达式数组溢出
        Assert(substr_len<32,"*** ERROR: Token too long! ***");//token字符串溢出
        switch (rules[i].token_type) {
          case(TK_NUM):
            // Clone string
            for(int p=0; p<substr_len; p++)
              tokens[nr_token].str[p]=substr_start[p];
            tokens[nr_token].str[substr_len]='\0';
            // Transfer type
            tokens[nr_token].type=rules[i].token_type;
            // Transfer status
            nr_token+=1;
            break;
          case(TK_HEX):
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
      printf("no match at position %d\n%s\n%*.s^\n", position, e, position, "");
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

bool is_ope_pri(int pri, int type){
  switch (pri)
  {
  case 4://第4优先级（加减逻辑法）
    if (type == '+' || type == '-' || (type > TK_AND && type < TK_OR)) return true;
    else return false;
  case 3://第3优先级（乘除法）
    if (type == '*' || type == '/') return true;
    else return false;
  case 2://第2优先级（单操作数）
    if (type == TK_MINUS || type == TK_DERE) return true;
    else return false;
  case 1://第1优先级（括号）
    if (type == '(' || type == ')') return true;
    else return false;
  default:
    return false;
  }
}


word_t eval(int p,int q,bool *success){
  if (p > q) {
    /* Bad expression */
    Log("*** ERROR: P > Q ! ***");
    return -1;
  }
  else if (p == q) {
    /* Single token.
     * For now this token should be a number.
     * Return the value of the number.
     */
    word_t immediate = 0;
    if(tokens[p].type == TK_HEX)//16进制情况
      sscanf(tokens[p].str, "%lxu", &immediate);
    else
      sscanf(tokens[p].str, "%lu", &immediate);
    Assert(immediate!=-1, "*** ERROR: Token number overflow! ***");
    return immediate;
  }
  else if (check_parentheses(p, q) == true) {
    /* The expression is surrounded by a matched pair of parentheses.
     * If that is the case, just throw away the parentheses.
     */
    Log("Check good p:%d,q:%d",p,q);
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
      if(tokens[i].type == '-'){//负号判断逻辑
        if(i == 0)
          tokens[i].type = TK_MINUS;// tokens
        else if(i == 0 ||( i > 0 && tokens[i-1].type != TK_NUM)){//去除前一位是数字位情况
          if(!is_ope_pri(1,tokens[i-1].type)){//去除前一位是括号位情况
            printf("该符是负号:%c,op%d,p:%d,q:%d\n",tokens[i].type,i,p,q);//即前一位只要是符号则该位符号为负号
            tokens[i].type = TK_MINUS;// tokens
          }
        }
      }
      else if(tokens[i].type == '*'){
        if(i == 0)
          tokens[i].type = TK_MINUS;// tokens
        else if(i == 0 ||( i > 0 && tokens[i-1].type != TK_NUM)){//去除前一位是数字位情况
          if(!is_ope_pri(1,tokens[i-1].type)){//去除前一位是括号位情况
            printf("该符是指针解引用符号:%c,op%d,p:%d,q:%d\n",tokens[i].type,i,p,q);//即前一位只要是符号则该位符号为指针解引用符
            tokens[i].type = TK_DERE;// tokens
          }
        }
      }
      else{//其他意外排除(即两个符号连在一起)
        if(i == 0 ||( i > 0 && tokens[i-1].type != TK_NUM)){//去除前一位是数字位情况
          if(!is_ope_pri(1,tokens[i-1].type)){//去除前一位是括号位情况
            Log("*** ERROR Cannot get main operation position!!! ***");
          }
        }
      } 

      //最外层的最低时记录op
      if(count == 0){
        if (is_ope_pri(4,tokens[i].type)){//第4优先级（加减逻辑法），
          op = i;
        } 
        else if (is_ope_pri(3,tokens[i].type)){//第3优先级（乘法），
          if(tokens[op].type != '+' && tokens[op].type != '-')//检测是否存在低优先级，
            op = i;                                           //如果有则op不变,从而进一步递归
        }
        else if (is_ope_pri(2,tokens[i].type)){//第2优先级（单操作数），
          if(!is_ope_pri(3,tokens[op].type)//检测op处是否存在低优先级，
          && !is_ope_pri(4,tokens[op].type)){//如果有则op不变,从而进一步递归
            if((i > p && tokens[i-1].type != TK_MINUS) || i == p)//-- 分割为右处 -(-)
              op = i;
          }
        }
      }
      //规则递进
      if(tokens[i].type == '(')
        count += 1;
      else if(tokens[i].type == ')')
        count -= 1;
      //printf("%c%s",tokens[i].type,tokens[i].str);
    }
    //找不到主运算符
    if(op<0){
      Log("*** ERROR Cannot get main operation position! ***");
      success = false;
      return -1;
    }
    //递归求值
    val1 = eval(p, op - 1, success);
    val2 = eval(op + 1, q, success);
    //类型转移
    op_type = tokens[op].type;
    // printf("主运算符:%c,val1:%ld,val2:%ld\n",op_type,val1,val2);
    switch (op_type) {
      case '+':       return val1 + val2;
      case '-':       return val1 - val2;
      case '*':       return val1 * val2;
      case '/':       return val1 / val2;
      case TK_MINUS:  return (-1) * val2;
      case TK_DERE:   return (*((word_t *)val2));
      default:{
        Log("*** ERROR: Operation %c not found ! ***",op_type);
        success = false;
        return -1;
      }  
    }
  }
}
//    (1+2)-(3+4)
// e  (1+5)*2+(6-3)
//    012345678901234
// b expr.c:188

// long long的最大值： 9223372036854775807
// long long的最小值： -9223372036854775808
// unsigned long long的最大值： 1844674407370955161

word_t expr(char *e, bool *success) {
  if (!make_token(e)) {
    *success = false;
    return 0;
  }

  // 对意外情况进行预测
  // for (int i = 0; i < nr_token; i ++) {
  //   if (tokens[i].type == '*' && (i == 0 || tokens[i - 1].type == '*') ) {
  //     tokens[i].type = TK_DERE;
  //   }
  // }

  /* TODO: Insert codes to evaluate the expression. */
  //TODO();
  static word_t test = 666666166;
  printf("number:%ld\n addr:%p\n",test,&test);
  word_t result = eval(0,nr_token-1,success);
  printf("result:%ld\n",result);


  return 0;
}
