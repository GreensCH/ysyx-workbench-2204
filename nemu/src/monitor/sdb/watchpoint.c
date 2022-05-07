#include "sdb.h"

#define NR_WP 32

typedef struct watchpoint {
  int NO;
  struct watchpoint *next;

  /* TODO: Add more members if necessary */
  vaddr_t pc;
  word_t val_old;
} WP;

static WP wp_pool[NR_WP] = {};
static WP *head = NULL, *free_ = NULL;

void init_wp_pool() {
  int i;
  for (i = 0; i < NR_WP; i ++) {
    wp_pool[i].NO = i;
    wp_pool[i].next = (i == NR_WP - 1 ? NULL : &wp_pool[i + 1]);
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
WP* new_wp();

/*
* free_wp()将wp归还到free_链表中
*/
void free_wp(WP *wp);

