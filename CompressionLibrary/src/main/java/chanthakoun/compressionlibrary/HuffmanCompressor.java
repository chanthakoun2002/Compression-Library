/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chanthakoun.compressionlibrary;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileReader;
//import java.io.FileWriter;
import java.io.File;
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
public class HuffmanCompressor implements CompressionAlgorithm{

    public static class Node implements Comparable<Node> {
//        char character;
        Byte data;
        int frequency;
        Node left, right;

        public Node(byte data, int frequency, Node left, Node right) {
//            this.character = character;
            this.data = data;
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
    
    
    @Override
    public byte[] compress(byte[] data) {
        Map<Byte, Integer> frequencies = calculateFrequencies(data);
        
        Node root = buildHuffmanTree(frequencies);
        Map<Byte, String> huffmanCode = new HashMap<>();
        generateCodes(root, "", huffmanCode);

        StringBuilder encodedData = new StringBuilder();
        for (byte b : data) {
            encodedData.append(huffmanCode.get(b));
        }

        return toByteArray(encodedData.toString());
    }

    @Override
    public byte[] decompress(byte[] compressedData) {
        String encodedString = fromByteArray(compressedData);
        //bytes converted back to bits
        //tree is rebuilt using stored frequency map
        return decompress(null, encodedString).getBytes();
    }
    
    //reads a file and generates a frequency map
    public Map<Byte, Integer> calculateFrequencies(byte[] data) {
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : data) {
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        }
        return frequencies;
     
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            int c;
//            while ((c = reader.read()) != -1) {
//                char character = (char) c;
//                frequencies.put(character, frequencies.getOrDefault(character, 0) + 1);
//            }
//        }
        
    }
    
    //genarates huffman tree
    public Node buildHuffmanTree(Map<Byte, Integer> frequencies) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        
        //leaf nodes are added to queue
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            priorityQueue.add(new Node(entry.getKey(), entry.getValue(), null, null));
        }
        
        //combines low nodes until one remains
        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node((byte) 0, left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
            
        }

        return priorityQueue.poll();
        
    }
    
    public void generateCodes(Node root, String code, Map<Byte, String> huffmanCode) {
        if (root == null) {
            return;
        }

        //If a leaf node is found it is stored as code
        if (root.left == null && root.right == null) {
            huffmanCode.put(root.data, code);
        }

        //Traverse left and right
        generateCodes(root.left, code + '0', huffmanCode);
        generateCodes(root.right, code + '1', huffmanCode);
    }
    
    //compress file
    public void compressFile(String inputFile, String outputFile) throws IOException {
        byte[] fileData = readFile(inputFile);

        //calculate byte frequencies
        Map<Byte, Integer> frequencies = calculateFrequencies(fileData);
        Node root = buildHuffmanTree(frequencies);

        Map<Byte, String> huffmanCode = new HashMap<>();
        generateCodes(root, "", huffmanCode);

        StringBuilder encodedData = new StringBuilder();
        for (byte b : fileData) {
            encodedData.append(huffmanCode.get(b));  // Encode each byte
        }
        
        //store metadata
        StringBuilder metadata = new StringBuilder();
            for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            metadata.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }

        //wite encoded data as bytes to output
        writeFile(outputFile, (metadata.toString() + "\n" + encodedData.toString()).getBytes());
    }

    //decompress file
    public void decompressFile(String inputFile, String outputFile) throws IOException {
        byte[] compressedData = readFile(inputFile);
    
        // Convert byte array to string for parsing (can improve this later)
        String compressedText = new String(compressedData);
    
        // Extract metadata (frequency map)
        String[] parts = compressedText.split("\n", 2); // First part is metadata, second is encoded data
        String metadata = parts[0];  // Frequency map as a string
        String encodedString = parts[1];  // Compressed data
    
        // Rebuild the frequency map
        Map<Byte, Integer> frequencies = new HashMap<>();
        String[] entries = metadata.split(",");
        for (String entry : entries) {
            String[] keyValue = entry.split(":");
            frequencies.put(Byte.valueOf(keyValue[0]), Integer.valueOf(keyValue[1]));
        }

        // Rebuild the Huffman Tree
        Node root = buildHuffmanTree(frequencies);

        // Decompress the encoded data using the tree
        String decodedString = decompress(root, encodedString);

        // Write the decompressed data back to the file
        writeFile(outputFile, decodedString.getBytes());
    }
    
    //decompresion for encoded huffman data
    public String decompress(Node root, String encodedString) {
        StringBuilder result = new StringBuilder();
        Node current = root;
        for (char bit : encodedString.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            //if a lead node found, byte is aded
            if (current.left == null && current.right == null) {
                result.append((char) current.data.byteValue());
                current = root;  // Return to root for next character
            }
        }
        return result.toString();
    }
    
    //conver encoded bits string to byte array, for file
     private byte[] toByteArray(String encodedData) {
        int byteCount = (encodedData.length() + 7) / 8;
        byte[] result = new byte[byteCount];
        for (int i = 0; i < encodedData.length(); i++) {
            int byteIndex = i / 8;
            result[byteIndex] = (byte) ((result[byteIndex] << 1) | (encodedData.charAt(i) - '0'));
        }
        return result;
    }

    //Convert byte array back to bit string, for decompression
    private String fromByteArray(byte[] byteArray) {
        StringBuilder encodedData = new StringBuilder();
        for (byte b : byteArray) {
            for (int i = 7; i >= 0; i--) {
                encodedData.append((b >> i) & 1);
            }
        }
        return encodedData.toString();
    }

    //read a file as a byte array
    private byte[] readFile(String filePath) throws IOException {
        return java.nio.file.Files.readAllBytes(new File(filePath).toPath());
    }

    //write byte data to a file
    private void writeFile(String filePath, byte[] data) throws IOException {
        java.nio.file.Files.write(new File(filePath).toPath(), data);
    }
}
