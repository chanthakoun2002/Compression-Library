/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chanthakoun.compressionlibrary;

/**
 *
 * @author Chant
 */
public interface CompressionAlgorithm {
    byte[] compress(byte[] data);
    byte[]decompress(byte[] compressedData);
}
