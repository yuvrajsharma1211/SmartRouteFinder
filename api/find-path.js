function buildGraph(nodes, edges) {
  const graph = new Map();

  for (const node of nodes) {
    graph.set(node, []);
  }

  for (const edge of edges) {
    if (!graph.has(edge.from)) graph.set(edge.from, []);
    if (!graph.has(edge.to)) graph.set(edge.to, []);

    graph.get(edge.from).push(edge);
    graph.get(edge.to).push({ from: edge.to, to: edge.from, cost: edge.cost });
  }

  return graph;
}

function buildPath(start, end, parent) {
  const path = [];
  let current = end;

  while (current != null) {
    path.push(current);
    current = parent.get(current);
  }

  path.reverse();
  return path.length > 0 && path[0] === start ? path : [];
}

function calculatePathCost(path, graph) {
  let cost = 0;

  for (let index = 0; index < path.length - 1; index += 1) {
    const neighbors = graph.get(path[index]) || [];
    const next = path[index + 1];
    const edge = neighbors.find((item) => item.to === next);

    if (edge) {
      cost += edge.cost;
    }
  }

  return cost;
}

function dijkstra(start, end, graph) {
  const dist = new Map();
  const prev = new Map();
  const queue = [{ node: start, distance: 0 }];

  for (const node of graph.keys()) {
    dist.set(node, Number.POSITIVE_INFINITY);
  }
  dist.set(start, 0);

  while (queue.length > 0) {
    queue.sort((left, right) => left.distance - right.distance);
    const { node: current, distance } = queue.shift();

    if (current === end) break;
    if (distance > dist.get(current)) continue;

    for (const edge of graph.get(current) || []) {
      const newDistance = dist.get(current) + edge.cost;
      if (newDistance < dist.get(edge.to)) {
        dist.set(edge.to, newDistance);
        prev.set(edge.to, current);
        queue.push({ node: edge.to, distance: newDistance });
      }
    }
  }

  const path = buildPath(start, end, prev);
  return { path, cost: path.length ? dist.get(end) : -1 };
}

function dfs(start, end, graph) {
  const visited = new Set();
  const parent = new Map();

  function visit(current) {
    if (current === end) return true;
    visited.add(current);

    for (const edge of graph.get(current) || []) {
      if (!visited.has(edge.to)) {
        parent.set(edge.to, current);
        if (visit(edge.to)) return true;
      }
    }

    return false;
  }

  const found = visit(start);
  const path = buildPath(start, end, parent);
  return { path, cost: found ? calculatePathCost(path, graph) : -1 };
}

function bfs(start, end, graph) {
  const queue = [start];
  const visited = new Set([start]);
  const parent = new Map([[start, null]]);

  while (queue.length > 0) {
    const current = queue.shift();
    if (current === end) break;

    for (const edge of graph.get(current) || []) {
      if (!visited.has(edge.to)) {
        visited.add(edge.to);
        parent.set(edge.to, current);
        queue.push(edge.to);
      }
    }
  }

  const path = buildPath(start, end, parent);
  return { path, cost: path.length ? calculatePathCost(path, graph) : -1 };
}

module.exports = async (req, res) => {
  if (req.method !== 'POST') {
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }

  try {
    const { nodes, edges, source, destination, algorithm } = req.body || {};

    if (!nodes || !edges || !source || !destination || !algorithm) {
      res.status(400).json({ error: 'Missing required fields' });
      return;
    }

    const graph = buildGraph(nodes, edges);
    const normalizedAlgorithm = String(algorithm).toLowerCase();

    let result;
    switch (normalizedAlgorithm) {
      case 'dfs':
        result = dfs(source, destination, graph);
        break;
      case 'bfs':
        result = bfs(source, destination, graph);
        break;
      default:
        result = dijkstra(source, destination, graph);
        break;
    }

    res.status(200).json(result);
  } catch (error) {
    res.status(500).json({ path: [], cost: -1, error: error.message });
  }
};