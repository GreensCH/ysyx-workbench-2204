#include <isa.h>

/* We use the POSIX regex functions to process regular expressions.
 * Type 'man regex' for more information about POSIX regex functions.
 */
#include <regex.h>

enum {
  TK_NOTYPE = 256, TK_EQ,

  /* TODO: Add more token types */
  TK_NEQ,TK_NUM,
};

static struct rule {
  const char *regex;
  int token_type;
} rules[] = {

  /* TODO: Add more rules.
   * Pay attention to the precedence level of different rules.
   */

  {" +", TK_NOTYPE},    // spaces
  {"\\+", '+'},         // plus
  {"==", TK_EQ},        // equal
  {"!=", TK_NEQ},       // not equal
  {"[0-9]+", TK_NUM},   // number
  {"\\-", '-'},         // sub
  {"\\*", '*'},         // mul
  {"\\/", '/'},         // div
  {"\\(", '('},         // left bracket
  {"\\)", ')'},         // right bracket
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

        // Log("match rules[%d] = \"%s\" at position %d with len %d: %.*s",
        //     i, rules[i].regex, position, substr_len, substr_len, substr_start);

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
            nr_token+=1;
            break;
          case(TK_EQ):;break;
          case(TK_NEQ):;break;
          case('-'):
          case('+'):
          case('*'):
          case('/'):
          case('('):
          case(')'):
            // Transfer type
            tokens[nr_token].type=rules[i].token_type;
            nr_token+=1;
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

word_t eval(int p,int q,bool *success){
  if (p > q) {
    /* Bad expression */
    return -1;
  }
  else if (p == q) {
    /* Single token.
     * For now this token should be a number.
     * Return the value of the number.
     */
    word_t immediate = 0;
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

    for(int i = p; i < q + 1; i++){
      if(tokens[i].type != TK_NUM){
        op_type = tokens[i].type;
        op = i;
      }
    }
    printf("p:%d,q:%d op_type:%c\t op:%d\n",p,q,op_type,op);
    if(op_type==')'){
      for(int i = p; i < q + 1; i++){
        printf("%c\t%s\t",tokens[i].type,tokens[i].str);
      }
      printf("\n");
      eval(p,op+1,success);
    }
    // op = the position of 主运算符 in the token expression;

    // for(int i = p; i < q + 1; i++){
    //   if(tokens[i].type == '+' || tokens[i].type == '-' 
    //   || tokens[i].type == '*' || tokens[i].type == '/'){
    //     op_type = tokens[i].type;
    //     op = i;
    //   }
    // }
    // if(op_type == -1)
    //   return false;

    val1 = eval(p, op - 1, success);
    val2 = eval(op + 1, q, success);
    printf("主运算符:%c val1:%ld,val2:%ld\n",op_type,val1,val2);
    switch (op_type) {
      case '+': return val1 + val2;
      case '-': return val1 - val2;
      case '*': return val1 * val2;
      case '/': return val1 / val2;
      default: Assert(0, "*** ERROR: Operation %c not found ***",op_type);
    }
  }
}// e ((1+5)*2+(6+3))
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

  /* TODO: Insert codes to evaluate the expression. */
  //TODO();
  word_t result = eval(0,nr_token-1,success);
  printf("result:%ld\n",result);
  // printf("p:%d \t q:%d\n",tokens[0].type,tokens[nr_token-1].type);
  // printf("%d\n",check_parentheses(0,nr_token-1));
  // Assert(check_parentheses(0,nr_token-1),"*** ERROR Check parentheses invalid");

  // printf("1\t%s:%ld\n",tokens[0].str,immdiate);

  return 0;
}
