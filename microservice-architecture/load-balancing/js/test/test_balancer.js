import {
  LeastConnectionsBalancer,
  RoundRobinBalancer,
  WeightedRoundRobinBalancer
} from '../src/balancer.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const roundRobin = new RoundRobinBalancer([
  { backendId: 'node-a' },
  { backendId: 'node-b' },
  { backendId: 'node-c' }
])
assertEqual('node-a', roundRobin.next().backendId, 'round robin first')
assertEqual('node-b', roundRobin.next().backendId, 'round robin second')
assertEqual('node-c', roundRobin.next().backendId, 'round robin third')

const weighted = new WeightedRoundRobinBalancer([
  { backendId: 'node-a', weight: 2 },
  { backendId: 'node-b', weight: 1 }
])
const counts = {}
for (let i = 0; i < 6; i++) {
  const backendId = weighted.next().backendId
  counts[backendId] = (counts[backendId] || 0) + 1
}
assertEqual(4, counts['node-a'], 'node-a weighted count')
assertEqual(2, counts['node-b'], 'node-b weighted count')

const least = new LeastConnectionsBalancer([
  { backendId: 'node-a', activeConnections: 2 },
  { backendId: 'node-b', activeConnections: 0 },
  { backendId: 'node-c', activeConnections: 1 }
])
assertEqual('node-b', least.acquire().backendId, 'least busy backend')
least.release('node-b')

console.log('load-balancing(js) tests passed')
