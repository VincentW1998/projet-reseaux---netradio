#include "h_files/list.h"
#include "h_files/manager.h"


/* part of client code for LIST request */
int recv_for_list(int descr, int len) {
    char buff[10];
    int size_rec;
    size_rec = recv(descr, buff, 9 * sizeof(char), 0);
    buff[size_rec] = '\0';
    printf("%s\n", buff);
    char str_num_diff[3];
    memcpy(str_num_diff, & buff[5], 2);
    str_num_diff[2] = '\0';
    int num_diff = atoi(str_num_diff);

    if(len == 26) {
        recv_listFiles(descr, num_diff, len);
    }
    else {
        for (int i = 0; i < num_diff; i++) {
            char tmp[len+1];
            size_rec = recv(descr, tmp, (len) * sizeof(char), 0);
            tmp[size_rec] = '\0';
            int j = 0;
            char * tok = strtok(tmp, " ");
            char * array[6];
            while( tok != NULL) {
                array[j++] = tok;
                tok = strtok(NULL, " ");
            }
            printf("\tID : %s\n", array[1]);
            printf("\tMulticast Address : %s\n", array[2]);
            printf("\tMulticast Port : %s\n", array[3]);
            printf("\tIP Address: %s\n", array[4]);
            printf("\tPort : %s\n", array[5]);
        }
    }
    return 0;
}

int recv_listFiles(int descr, int nb, int len) {
    int size_rec;
    for (int i = 1; i <= nb; i++) {
        char * tmp = malloc(sizeof(char) * len);
        size_rec = recv(descr, tmp, (len-1) * sizeof(char), 0);
        tmp[size_rec] = '\0';
        char * str = remove_hashtag(tmp);
        printf("if of %s : %d\n", str, i);
        free(tmp);
    }
    return 0;
}