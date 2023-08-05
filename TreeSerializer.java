import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;

public class TreeSerializer {

    private HashSet<Node> set;

    public String serialize(Node root) {
        set = new HashSet<Node>();
        return serializeHelper(root);
    }

    public String serializeHelper(Node root) {
        if (root == null) {
            return "null";
        }
        // Handle cycles
        if (set.contains(root)) {
            throw new RuntimeException("Cycle detected in tree.");
        } else {
            set.add(root);
        }
        String leftStr = serializeHelper(root.left);
        String rightStr = serializeHelper(root.right);
        
        return root.num + "," + leftStr + "," + rightStr;
    }

    public Node deserialize(String str) {
        Queue<String> nodes = new LinkedList<>(Arrays.asList(str.split(",")));
        return deserializeHelper(nodes);
    }
    
    private Node deserializeHelper(Queue<String> nodes) {
        String val = nodes.poll();
        if (val.equals("null")) {
            return null;
        }
        
        Node root = new Node(Integer.parseInt(val));
        root.left = deserializeHelper(nodes);
        root.right = deserializeHelper(nodes);
        
        return root;
    }

    public static void main(String[] args) {
        // Test the serializer and deserializer
        TreeSerializer treeSerializer = new TreeSerializer();

        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        
        String serializedTree = treeSerializer.serialize(root);
        System.out.println("Serialized tree: " + serializedTree);

        // cyclic case
        root.left.left.right = root.right;
        serializedTree = treeSerializer.serialize(root);
    }
}


/*  ------ANSWER TO 2.III---------
Instead of storing integers separated by commas, we could store JSON strings separated by semicolons with 2 fields: "type" and "value". 

type would store a string containing the name of the data type like "String".
value would store either a single value or a JSON string representing the fields of an object.
*/