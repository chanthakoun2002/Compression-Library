/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chanthakoun.compressionlibrary;


import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Chant
 */
public class Main {



//this is to test file compression and decompression for huffman algorithm
    public static void main(String[] args) throws IOException {
        HuffmanCompressor compressor = new HuffmanCompressor();
        
        String inputFilePath = "";
        String compressedFilePath = "";
        String decompressedFilePath = "";
        
        try {
            //compress file
            System.out.println("Compressing file...");
            compressor.compressFile(inputFilePath, compressedFilePath);
            System.out.println("File compressed successfully: " + compressedFilePath);
            
            Map<Character, Integer> frequencies = compressor.calculateFrequencies(inputFilePath);
            HuffmanCompressor.Node root = compressor.buildHuffmanTree(frequencies);

            //decompress file
            System.out.println("Decompressing file...");
            compressor.decompressFile(compressedFilePath, decompressedFilePath, root);
            System.out.println("File decompressed successfully: " + decompressedFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
