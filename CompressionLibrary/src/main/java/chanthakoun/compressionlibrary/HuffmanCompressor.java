/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chanthakoun.compressionlibrary;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
/**
 *
 * @author Chant
 * 
 * Builds the Huffman Tree from character frequencies.
 * 
 * param frequencies A map containing character frequencies
 * return The root of the Huffman Tree
 */
public class HuffmanCompressor {

    public static class Node implements Comparable<Node> {
        char character;
        int frequency;
        Node left, right;

        public Node(char character, int frequency, Node left, Node right) {
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
        //compare nodes based on frequency
        @Override
        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }
    }
    
    //reads a file and generates a frequency map
    public Map<Character, Integer> calculateFrequencies(String filePath) throws IOException {
        Map<Character, Integer> frequencies = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int c;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                frequencies.put(character, frequencies.getOrDefault(character, 0) + 1);
            }
        }
        return frequencies;
    }
    
    //genarates huffman tree
    public Node buildHuffmanTree(Map<Character, Integer> frequencies) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        
        //leaf nodes are added to queue
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            priorityQueue.add(new Node(entry.getKey(), entry.getValue(), null, null));
        }
        
        //combines low nodes until one remains
        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
            
        }

        return priorityQueue.poll();
        
    }
    
    public void generateCodes(Node root, String code, Map<Character, String> huffmanCode) {
        if (root == null) {
            return;
        }

        //If a leaf node is found it is stored as code
        if (root.left == null && root.right == null) {
            huffmanCode.put(root.character, code);
        }

        //Traverse left and right
        generateCodes(root.left, code + '0', huffmanCode);
        generateCodes(root.right, code + '1', huffmanCode);
    }
    
    //compress file
    public void compressFile(String inputFile, String outputFile) throws IOException {
        Map<Character, Integer> frequencies = calculateFrequencies(inputFile);
        Node root = buildHuffmanTree(frequencies);

        Map<Character, String> huffmanCode = new HashMap<>();
        generateCodes(root, "", huffmanCode);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            int c;
            while ((c = reader.read()) != -1) {
                writer.write(huffmanCode.get((char) c));  // Write the corresponding Huffman code
            }
        }
    }

    //decompress file
    public void decompressFile(String inputFile, String outputFile, Node root) throws IOException {
        StringBuilder encodedString = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            int c;
            while ((c = reader.read()) != -1) {
                encodedString.append((char) c);
            }
        }

        //decompress the data using the Huffman Tree
        String decodedString = decompress(root, encodedString.toString());

        // Write the decompressed data to the output file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(decodedString);
        }
    }
    
    //decompresion
    public String decompress(Node root, String encodedString) {
    StringBuilder result = new StringBuilder();
    Node current = root;
    for (char bit : encodedString.toCharArray()) {
        current = (bit == '0') ? current.left : current.right;
        
        // If we hit a leaf node, adds character to result
        if (current.left == null && current.right == null) {
            result.append(current.character);
            current = root; //return to root for the next character
        }
    }
    return result.toString();
}
}
