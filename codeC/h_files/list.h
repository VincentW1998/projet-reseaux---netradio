#ifndef LIST_H
#define LIST_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>

int recv_for_list(int descr, int len);

int recv_listFiles(int descr, int nb, int len);

#endif