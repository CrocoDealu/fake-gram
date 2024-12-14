package org.example.networkManagement;

public class SocialComunities {
//    private final SocialNetwork socialNetwork;
//
//    Map<Long, List<Long>> adjList;
//
//    public SocialComunities(SocialNetwork socialNetwork) {
//        this.socialNetwork = socialNetwork;
//    }
//
//    private void DFS(long v, HashMap<Long, Boolean> visited) {
//        visited.put(v, true);
//        if (adjList.containsKey(v)) {
//            adjList.get(v).forEach((friend) -> {
//                if (!visited.get(friend)) {
//                    DFS(friend, visited);
//                }
//            });
//        }
//    }
//
//    private void initializeAdjList() {
//        adjList = new HashMap<>();
//        socialNetwork.getUsers().forEach((user) -> {
//            List<Long> friends = new ArrayList<>();
//            socialNetwork.getFriendships().forEach((friendship -> {
//                if (friendship.getUsername1().equals(user.getId()))
//                    friends.add(friendship.getUsername2());
//                if (friendship.getUsername2().equals(user.getId()))
//                    friends.add(friendship.getUsername1());
//            }));
//            if (!friends.isEmpty())
//                adjList.put(user.getId(), friends);
//        });
//    }
//
//    public int comunitiesNumber() {
//        initializeAdjList();
//
//        List<Long> ids = new ArrayList<>();
//        HashMap<Long, Boolean> visited = new HashMap<Long, Boolean>();
//        socialNetwork.getUsers().forEach(user -> {
//            ids.add(user.getId());
//            visited.put(user.getId(), false);
//        });
//
//        AtomicInteger noComunities = new AtomicInteger();
//
//        ids.forEach((id) -> {
//            if (!visited.get(id)) {
//                DFS(id, visited);
//                noComunities.getAndIncrement();
//            }
//        });
//        return noComunities.get();
//    }
//
//    public List<Long> mostSociableNetwork() {
//        initializeAdjList();
//        AtomicReference<List<Long>> max = new AtomicReference<>(new ArrayList<>());
//        HashMap<Long, Boolean> visited = new HashMap<Long, Boolean>();
//        for (User u: socialNetwork.getUsers()) {
//            visited.put(u.getId(), false);
//        }
//        socialNetwork.getUsers().forEach((user) -> {
//            if (adjList.containsKey(user.getId()) && !visited.get(user.getId())) {
//                Long farthest = BFS(user.getId(), visited);
//                List<Long> friends;
//                friends = BFSPath(farthest);
//                if (friends.size() > max.get().size()) {
//                    max.set(friends);
//                }
//            }
//        });
//
//        return max.get();
//    }
//
//    private List<Long> BFSPath(Long start) {
//        Queue<Long> queue = new LinkedList<>();
//        HashMap<Long, Long> parentMap = new HashMap<>();
//        HashMap<Long, Boolean> visited = new HashMap<>();
//        Long farthestNode = start;
//
//        queue.add(start);
//        visited.put(start, true);
//        parentMap.put(start, null);
//
//        while (!queue.isEmpty()) {
//            farthestNode = queue.poll();
//
//            Long finalFarthestNode = farthestNode;
//            adjList.get(farthestNode).forEach((neighbor) -> {
//                if (!visited.getOrDefault(neighbor, false)) {
//                    visited.put(neighbor, true);
//                    parentMap.put(neighbor, finalFarthestNode);
//                    queue.add(neighbor);
//                }
//            });
//        }
//
//        List<Long> longestPath = new ArrayList<>();
//        for (Long node = farthestNode; node != null; node = parentMap.get(node)) {
//            longestPath.add(node);
//        }
//        Collections.reverse(longestPath);
//
//        return longestPath;
//    }
//
//    private Long BFS(Long root, HashMap<Long, Boolean> visited) {
//        Queue<LongIntPair> queue = new LinkedList<LongIntPair>();
//
//        List<Long> toReset = new ArrayList<>();
//        LongIntPair rootP = new LongIntPair(root, 0);
//        queue.add(rootP);
//        visited.put(root, true);
//        toReset.add(root);
//        long max = 0L;
//        long maxUid = 0L;
//        while (!queue.isEmpty()) {
//            LongIntPair currentPair = queue.poll();
//            long uid = currentPair.getFirst();
//            int depth = currentPair.getSecond();
//            if (depth > max) {
//                max = depth;
//                maxUid = uid;
//            }
//            adjList.get(uid).forEach((ui) -> {
//                if (!visited.get(ui)) {
//                    queue.add(new LongIntPair(ui, depth + 1));
//                    visited.put(ui, true);
//                    toReset.add(ui);
//                }
//            });
//        }
//        toReset.forEach((vi) -> {
//            visited.put(vi, false);
//        });
//        return maxUid;
//    }
}
