#include "sdb.h"

#define NR_WP 32

typedef struct watchpoint {
  int NO;
  struct watchpoint *next;

  /* TODO: Add more members if necessary */
  int type;//0 watch 1 break
  vaddr_t pc;
  word_t val_old;
  char expr[32];
} WP;

static WP wp_pool[NR_WP] = {};
static WP *head = NULL, *free_ = NULL;

void init_wp_content(WP *p){
  p -> type = 0;
  p -> pc = 0;
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
    Assert(0, "*** ERROR Watch-Pool Overflow***");
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
  for(WP *p = head; p -> next != NULL; p = p->next){
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
  for(; p -> next != NULL; p = p->next){
    if(p -> NO == NO){
      *res = p;
      return;
    }
  }
  return;
}

void find_idle_wp(int NO, WP** res){
  WP *p = free_;
  for(; p -> next != NULL; p = p->next){
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

void new_wp_expr(char *args){
  bool success = false;
  /* word_t val = */
  if(args != NULL)
    expr(args, &success);
  WP* p = new_wp();

  printf("-head-id:");
  if(head!=NULL) printf("%4d,next:",head->NO);
  else printf("NULL,next:");
  if(head!=NULL&&head->next!=NULL) printf("%4d,\n",head->next->NO);
  else printf("NULL,\n");

  printf("-free-id:");
  if(free_!=NULL) printf("%4d,next:",free_->NO);
  else printf("NULL,next:");
  if(free_!=NULL&&free_->next!=NULL) printf("%4d,\n",free_->next->NO);
  else printf("NULL,\n");

  printf("-addp-id:");
  if(p!=NULL) printf("%4d,next:",p->NO);
  else printf("NULL,next:");
  if(p!=NULL&&p->next!=NULL) printf("%4d,\n",p->next->NO);
  else printf("NULL,\n");
}

void delete_wp_expr(char *args){
  bool success = false;
  /* word_t val = */
  if(args != NULL)
    expr(args, &success);
  
  int NO = atoi(args);
  printf("Delete point is:%d\n",NO);
  printf("*** Delete Prepering ***\n");
  WP* p = NULL;
  find_all_wp(NO, &p);

  printf("-head-id:");
  if(head!=NULL) printf("%4d,next:",head->NO);
  else printf("NULL,next:");
  if(head!=NULL&&head->next!=NULL) printf("%4d,\n",head->next->NO);
  else printf("NULL,\n");

  printf("-free-id:");
  if(free_!=NULL) printf("%4d,next:",free_->NO);
  else printf("NULL,next:");
  if(free_!=NULL&&free_->next!=NULL) printf("%4d,\n",free_->next->NO);
  else printf("NULL,\n");

  printf("-delp-id:");
  if(p!=NULL) printf("%4d,next:",p->NO);
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

  printf("-head-id:");
  if(head!=NULL) printf("%4d,next:",head->NO);
  else printf("NULL,next:");
  if(head!=NULL&&head->next!=NULL) printf("%4d,\n",head->next->NO);
  else printf("NULL,\n");

  printf("-free-id:");
  if(free_!=NULL) printf("%4d,next:",free_->NO);
  else printf("NULL,next:");
  if(free_!=NULL&&free_->next!=NULL) printf("%4d,\n",free_->next->NO);
  else printf("NULL,\n");

  printf("-delp-id:");
  if(p!=NULL) printf("%4d,next:",p->NO);
  else printf("NULL,next:");
  if(p!=NULL&&p->next!=NULL) printf("%4d,\n",p->next->NO);
  else printf("NULL,\n");

}


void _wp_display(WP *p){
  if(p != NULL)
    Log("*** ERROR Cannot display current watch point ***");
  else if(p ->type ==0){//watch point
    printf("watch point:%d", p -> NO);
    printf(",expr:%s\n", p -> expr);
    //printf(",value:%d", p -> val_old);
  }
}

void _test_wp_display(WP *p){
  if(p != NULL)
    Log("*** ERROR Cannot display current watch point ***");
  else if(p ->type ==0){//watch point
    printf("watch point:%d", p -> NO);
    printf(",expr:%s", p -> expr);
    if(p!=NULL&&p->next!=NULL) printf(",next:%4d,\n",p->next->NO);
    else printf(",next:NULL,\n");
  }
}

void break_point_display(){
  WP *p = head;
  for(; p -> next != NULL; p = p->next){
    _wp_display(p);
  }
}

void watch_point_display(){
  WP *p = head;
  Log("** HEAD **");
  for(; p -> next != NULL; p = p->next){
    _test_wp_display(p);
  }
  Log("** FREE **");
  p = free_;
  for(; p -> next != NULL; p = p->next){
    _test_wp_display(p);
  }
}