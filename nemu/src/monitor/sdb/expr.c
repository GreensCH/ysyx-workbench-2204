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
            //Log("Number has added to the sequence.");
            for(int p=0; p<substr_len; p++)
              tokens[nr_token].str[p]=substr_start[i];
            tokens[nr_token].str[substr_len]='\0';
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
            //Log("Symbol has added to the sequence.");
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

  // for(int j=0;j<nr_token;j++){
  //   printf("%d:%s\n",tokens[j].type,tokens[j].str);
  // }

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
  int i;
  for(i = p; i < q + 1; i++){
    if(tokens[i].type == '(')
      count +=1;
    else if(tokens[i].type == ')')
      count -=1;
    
    if(count<0)
      return false;
  }
  if(count)
    return false;
  else
    return true;
}

// word_t eval(int p,int q,bool *success){
//   if (p > q) {
//     /* Bad expression */
//   }
//   else if (p == q) {
//     /* Single token.
//      * For now this token should be a number.
//      * Return the value of the number.
//      */
//   }
//   else if (check_parentheses(p, q) == true) {
//     /* The expression is surrounded by a matched pair of parentheses.
//      * If that is the case, just throw away the parentheses.
//      */
//     return eval(p + 1, q - 1);
//   }
//   else {
//     op = the position of 主运算符 in the token expression;
//     val1 = eval(p, op - 1);
//     val2 = eval(op + 1, q);

//     switch (op_type) {
//       case '+': return val1 + val2;
//       case '-': /* ... */
//       case '*': /* ... */
//       case '/': /* ... */
//       default: assert(0);
//     }
//   }
// }


word_t expr(char *e, bool *success) {
  if (!make_token(e)) {
    *success = false;
    return 0;
  }

  /* TODO: Insert codes to evaluate the expression. */
  //TODO();
  // word_t res = eval(0,nr_token-1,success);
  printf("p:%d \t q:%d\n",tokens[0].type,tokens[nr_token-1].type);
  bool temp = check_parentheses(0,nr_token-1);
  Assert(temp,"*** ERROR Check parentheses invalid");
  return 0;
}
