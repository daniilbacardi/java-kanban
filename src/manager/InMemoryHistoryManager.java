package manager;

import tasksTypes.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList tasksList = new CustomLinkedList();

    @Override
    public void add(Task task) {
        tasksList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        tasksList.removeNode(tasksList.getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return tasksList.getWatchedTasks();
    }

    static class CustomLinkedList {
        private final Map<Integer, Node> watchedTasks = new HashMap<>();
        private Node first;
        private Node last;

        private void linkLast(Task task) {
            Node element = new Node();
            element.setTask(task);

            if (watchedTasks.containsKey(task.getId())) {
                removeNode(watchedTasks.get(task.getId()));
            }

            if (first == null) {
                last = element;
                first = element;
                element.setNext(null);
                element.setPrev(null);
            } else {
                element.setPrev(last);
                element.setNext(null);
                last.setNext(element);
                last = element;
            }
            watchedTasks.put(task.getId(), element);
        }

        private List<Task> getWatchedTasks() {
            List<Task> result = new ArrayList<>();
            Node element = first;
            while (element != null) {
                result.add(element.getTask());
                element = element.getNext();
            }
            return result;
        }

        private void removeNode(Node node) {
            if (node != null) {
                watchedTasks.remove(node.getTask().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (first == node) {
                    first = node.getNext();
                }
                if (last == node) {
                    last = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }

                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        private Node getNode(int id) {
            return watchedTasks.get(id);
        }
    }
}
