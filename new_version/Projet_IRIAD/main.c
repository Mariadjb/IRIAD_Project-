#include <stdio.h>
#include <stdlib.h>

struct cell {
    int data;
    struct cell *prev;
    struct cell *next;
};

struct list {
    struct cell *tail, *head;
};

typedef struct cell *ptr;

void allocate(ptr *p) {
    *p = (struct cell *)malloc(sizeof(struct cell));
}

void Free(ptr p) {
    free(p);
}

ptr nexte(ptr p) {
    return p->next;
}

ptr previ(ptr p) {
    return p->prev;
}

void ass_adrg(ptr p, ptr q) {
    p->prev = q;
}

void ass_adrd(ptr p, ptr q) {
    p->next = q;
}

int value(ptr p) {
    return p->data;
}

void ass_val(ptr p, int val) {
    p->data = val;
}

void createdlist(struct list *l, int n) {
    int i, val;
    ptr p, q;

    allocate(&p);
    printf("Entrer la première valeur: ");
    scanf("%d", &val);
    ass_val(p, val);
    ass_adrg(p, NULL);
    ass_adrd(p, NULL);  // Update this line to link the first node's next pointer to NULL
    l->head = p;

    for (i = 2; i <= n; i++) {
        allocate(&q);
        printf("Entrer la valeur: ");
        scanf("%d", &val);
        ass_val(q, val);
        ass_adrg(q, p);
        ass_adrd(q, NULL);  // Update this line to link the current node's next pointer to NULL
        ass_adrd(p, q);     // Update this line to link the previous node's next pointer to the current node
        p = q;
    }

    l->tail = p;
}


void displaydlist(ptr l, int n) {
    ptr p = l;
    printf("here is the list : \n");

    while (p != NULL) {
        printf("%d ", value(p));
        p = nexte(p);
    }
    printf("\n");
}

int main() {
    int n;
    printf("Entrer le nombre de cellules: ");
    scanf("%d", &n);

    struct list l;
    createdlist(&l, n);
    displaydlist(l.head, n);

    return 0;
}
