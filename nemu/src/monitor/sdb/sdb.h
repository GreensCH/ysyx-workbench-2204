#ifndef __SDB_H__
#define __SDB_H__

#include <common.h>

word_t expr(char *e, bool *success);

void new_wp_expr(char *args);
void delete_wp_expr(char *args);

void break_point_display();
void watch_point_display();

#endif
