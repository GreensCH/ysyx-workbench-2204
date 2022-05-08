#include "sdb.h"

#define NR_WP 32

typedef struct watchpoint {
  int NO;
  struct watchpoint *next;

  /* TODO: Add more members if necessary */
  int id;
  int type;//0 watch 1 break
  vaddr_t pc;
  word_t val_old;
  char expr32[32];
} WP;

static WP wp_pool[NR_WP] = {};
static WP *head = NULL, *free_ = NULL;

void init_wp_content(WP *p){
  p -> id   = -1;
  p -> type = 0;
  p -> pc   = 0;
  p -> val_old = 0;
}

void init_wp_pool() {
  int i;
  for (i = 0; i < NR_WP; i ++) {
    wp_pool[i].NO = i;
    wp_pool[i].next = (i == NR_WP - 1 ? NULL : &wp_pool[i + 1]);
    init_wp_content(&(wp_pool[i]));
  }

  head = NULL;
  free_ = wp_pool;
}

/* TODO: Implement the functionality of watchpoint */

/* 
* new_wp(),free_wp()监视点池的接口，被其它函数调用
* 
*/

/*
* new_wp()从free_链表中返回一个空闲的监视点结构
*/
WP* new_wp(){
  if(free_ == NULL)
    return NULL;//Assert(0, "*** ERROR Watch-Pool Overflow***");
  else{
    WP *old_head_wp = head;
    head = free_;// head forward 1
    free_ = free_ -> next;// free back 1
    head -> next = old_head_wp;//change add point's next
    return head;
  }
}

/*
* free_wp()将wp归还到free_链表中
*/
void free_wp(WP *wp){
  WP *old_p = NULL;
  for(WP *p = head; p != NULL; p = p->next){
    if(p -> NO == wp -> NO){
      if(old_p != NULL)
        old_p -> next = p ->next;//?->next=D
      else
        head = head ->next;
      p -> next = free_;//D->next=free
      free_ = p;//free=D
      init_wp_content(p);
      break;
    }
    old_p = p;
  }
}

void find_active_wp(int NO, WP** res){
  WP *p = head;
  for(; p != NULL; p = p->next){
    if(p -> NO == NO){
      *res = p;
      return;
    }
  }
  return;
}

void find_idle_wp(int NO, WP** res){
  WP *p = free_;
  for(; p != NULL; p = p->next){
    if(p -> NO == NO){
      *res = p;
      return;
    }
  }
  return;
}

void find_all_wp(int NO, WP** res){
  find_active_wp(NO, res);
  find_idle_wp(NO, res);
  return;
}

void find_active_wp_byid(int id, WP** res){
  WP *p = head;
  for(; p != NULL; p = p->next){
    if(p -> id == id){
      *res = p;
      return;
    }
  }
  return;
}

static int nr_watchpoint = 0;

void new_wp_expr(char *args, bool *success){
  //参数读取
  if(args == NULL){
    Log("*** Add fail, please point out watch point ***");
    *success = false;
    return;
  }
  //申请wp
  WP* p = new_wp();
  if(p == NULL){
    *success = false;
    Log("*** ERROR Watch-Pool Overflow***");
    return;
  }
  //存储wp
  nr_watchpoint += 1;
  p -> id = nr_watchpoint;
  strcpy(p -> expr32, args);
  p -> val_old = expr(args,success);
}

void delete_wp_expr(char *args, bool *success){
  //参数读取
  if(args == NULL){
    Log("*** Delete fail, please point out watch point ***");
    *success = false;
    return;
  }
  int id = atoi(args);
  printf("Delete point is:%d\n",id);
  printf("*** Delete Prepering ***\n");
  
  //寻找wp
  WP* p = NULL;
  find_active_wp_byid(id, &p);
  if(p == NULL){
    *success = false;
    Log("*** ERROR Cannot found watch point ***");
    return;
  }

  //打印信息
  printf("-head-no:");
  if(head!=NULL) printf("%4d,next:",head->NO);
  else printf("NULL,next:");
  if(head!=NULL&&head->next!=NULL) printf("%4d,\n",head->next->NO);
  else printf("NULL,\n");

  printf("-free-no:");
  if(free_!=NULL) printf("%4d,next:",free_->NO);
  else printf("NULL,next:");
  if(free_!=NULL&&free_->next!=NULL) printf("%4d,\n",free_->next->NO);
  else printf("NULL,\n");

  printf("-delp-no:");
  if(p!=NULL) printf("%4d,id:%d,next:",p->NO,p->id);
  else printf("NULL,next:");
  if(p!=NULL&&p->next!=NULL) printf("%4d,\n",p->next->NO);
  else printf("NULL,\n");

  if(p==NULL){
    printf("*** Cannot found watch point ***\n");
    return ;
  }
  else{
    free_wp(p);
    printf("*** Delete Finish ***\n");
  }

  printf("-head-no:");
  if(head!=NULL) printf("%4d,next:",head->NO);
  else printf("NULL,next:");
  if(head!=NULL&&head->next!=NULL) printf("%4d,\n",head->next->NO);
  else printf("NULL,\n");

  printf("-free-no:");
  if(free_!=NULL) printf("%4d,next:",free_->NO);
  else printf("NULL,next:");
  if(free_!=NULL&&free_->next!=NULL) printf("%4d,\n",free_->next->NO);
  else printf("NULL,\n");

  printf("-delp-no:");
  if(p!=NULL) printf("%4d,id:%d,next:",p->NO,p->id);
  else printf("NULL,next:");
  if(p!=NULL&&p->next!=NULL) printf("%4d,\n",p->next->NO);
  else printf("NULL,\n");

}


void wp_display(WP *p, word_t val_new){
  if(p == NULL)
    Log("*** ERROR Cannot display current watch point ***");
  else if(p ->type == 0){//watch point
    printf("watch point:%d,",p -> id);
    printf("expr:%s,",        p -> expr32);
    printf("old value:%ld,",  p -> val_old);
    printf("new value:%ld\n", val_new);
  }
  else if(p ->type == 1){//break point
    printf("break point:%d,",p -> id);
    printf("expr:%s,",       p -> expr32);
    printf("value:%ld,\n",   p -> val_old);
  }
}

void wp_full_display(WP *p, word_t val_new){
  if(p == NULL)
    Log("*** ERROR Cannot display current watch point ***");
  else if(p ->type == 0){//watch point
    // bool success = false;
    printf("watch point:%d,",p -> id);
    printf("expr:%s,",        p -> expr32);
    printf("old value:%ld,",  p -> val_old);
    printf("new value:%ld\n", val_new);
  }
  else if(p ->type == 1){//break point
    // bool success = false;
    printf("break point:%d,",p -> id);
    printf("expr:%s,",        p -> expr32);
    printf("old value:%ld,\n",  p -> val_old);
    // printf("new value:%ld\n", expr(p -> expr32, &success));
  }
}

void test_wp_display(WP *p){
  if(p == NULL)
    Log("*** ERROR Cannot display current watch point ***");
  else if(p ->type ==0){//watch point
    printf("watch point:%d", p -> NO);
    printf(",expr:%s", p -> expr32);
    if(p!=NULL&&p->next!=NULL) printf(",next:%4d,\n",p->next->NO);
    else printf(",next:NULL,\n");
  }
}

void break_point_display(){
  WP *p = head;
  for(; p != NULL; p = p->next){
    wp_full_display(p, 0);
  }
}

void wp_list_display(){
  printf("** Watch Points **\n");
  WP *p = head;
  for(;p != NULL; p = p->next){
    wp_full_display(p, 0);
  }
}

void test_wp_list_display(){
  printf("** HEAD **\n");
  WP *p = head;
  for(;p != NULL; p = p->next){
    test_wp_display(p);
  }
  printf("** FREE **\n");
  p = free_;
  for(;p != NULL; p = p->next){
    test_wp_display(p);
  }
}

//添加时
// (gdb) watch nr_watchpoint
// Hardware watchpoint 1: nr_watchpoint

//改变时

// Hardware watchpoint 1: nr_watchpoint

// Old value = 0
// New value = 1
// new_wp_expr (args=0x55555d5dd9a6 "1", success=success@entry=0x7fffffffda87) at src/monitor/sdb/watchpoint.c:138
// 138       p -> id = nr_watchpoint;

bool wp_exec(){
  WP *p = head;
  word_t val_new = 0;
  bool success = false;
  bool is_exec = false;
  for(; p != NULL; p = p->next){
    val_new = expr(p->expr32, &success);
    if(val_new != p->val_old){
      wp_display(p, val_new);
      p -> val_old = val_new;
      is_exec = true;
      printf("is_exec:%d\n",is_exec);
    }
  }
  return is_exec;
}