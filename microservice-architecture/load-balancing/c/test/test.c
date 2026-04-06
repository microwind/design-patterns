#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    Backend backends[] = {
        {"node-a", 2, 2},
        {"node-b", 1, 0},
        {"node-c", 1, 1}
    };

    RoundRobinBalancer rr;
    rr_init(&rr, backends, 3);
    assert(strcmp(rr_next(&rr)->backend_id, "node-a") == 0);
    assert(strcmp(rr_next(&rr)->backend_id, "node-b") == 0);
    assert(strcmp(rr_next(&rr)->backend_id, "node-c") == 0);

    WeightedRoundRobinBalancer wrr;
    wrr_init(&wrr, backends, 2);
    int node_a = 0;
    int node_b = 0;
    for (int i = 0; i < 6; i++) {
        const char *backend_id = wrr_next(&wrr)->backend_id;
        if (strcmp(backend_id, "node-a") == 0) {
            node_a++;
        } else if (strcmp(backend_id, "node-b") == 0) {
            node_b++;
        }
    }
    assert(node_a == 4);
    assert(node_b == 2);

    LeastConnectionsBalancer lc;
    lc_init(&lc, backends, 3);
    assert(strcmp(lc_acquire(&lc)->backend_id, "node-b") == 0);
    lc_release(&lc, "node-b");

    printf("load-balancing(c) tests passed\n");
    return 0;
}
