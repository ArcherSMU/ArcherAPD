package org.example;

import org.example.model.CrackResult;
import org.example.model.User;
import org.example.service.*;

import java.util.List;

/**
 * Main application orchestrator for the dictionary attack tool.
 * Coordinates the password cracking workflow using distinct components:
 * 1. TargetHashManager - Loads target hashes
 * 2. ProgressTracker - Reports live status
 * 3. CrackingEngine - Core concurrent cracking engine
 */
public class DictionaryAttackApplication {
    
    private final FileService fileService;
    private final TargetHashManager targetHashManager;
    private final PasswordCracker passwordCracker;
    private final ProgressTracker progressTracker;
    
    public DictionaryAttackApplication() {
        this.fileService = new FileService();
        this.targetHashManager = new TargetHashManager();
        this.progressTracker = new ProgressTracker();
        
        // Create the core cracking engine (Parallel Streams - benchmarked fastest)
        HashService hashService = new HashService();
        CrackingEngine crackingEngine = new ParallelStreamsCrackingEngine(hashService, progressTracker);
        
        this.passwordCracker = new PasswordCracker(crackingEngine, progressTracker);
    }
    
    /**
     * Executes the password cracking operation.
     * 
     * @param usersPath Path to the users file
     * @param dictionaryPath Path to the dictionary file
     * @param outputPath Path to the output file
     */
    public void execute(String usersPath, String dictionaryPath, String outputPath) {
        try {
            // Component 1: Load target hashes
            List<User> users = targetHashManager.loadTargetHashes(usersPath);
            
            // Load dictionary
            List<String> dictionary = fileService.loadDictionary(dictionaryPath);
            
            // Component 3: Crack passwords using the engine
            CrackResult result = passwordCracker.crack(users, dictionary);
            
            // Component 2: Report final results
            progressTracker.reportFinalStats(
                    result.getPasswordsFound(),
                    result.getHashesComputed(),
                    result.getDurationMillis()
            );
            
            // Write output
            if (result.getPasswordsFound() > 0) {
                fileService.writeCrackedPasswords(outputPath, users);
                System.out.println("\nCracked password details have been written to " + outputPath);
            }
            
        } catch (Exception e) {
            System.err.println("Error during password cracking: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar <jar-file-name>.jar <input_file> <dictionary_file> <output_file>");
            System.exit(1);
        }
        
        String usersPath = args[0];
        String dictionaryPath = args[1];
        String outputPath = args[2];
        
        DictionaryAttackApplication app = new DictionaryAttackApplication();
        app.execute(usersPath, dictionaryPath, outputPath);
    }
}
