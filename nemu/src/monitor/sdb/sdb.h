#ifndef __SDB_H__
#define __SDB_H__

#include <common.h>

word_t expr(char *e, bool *success);

void new_wp_expr(char *args, bool *success);
void delete_wp_expr(char *args, bool *success);


void wp_list_display();

#endif
