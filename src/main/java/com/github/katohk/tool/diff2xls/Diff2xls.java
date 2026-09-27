package com.github.katohk.tool.diff2xls;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.github.katohk.tool.diff2xls.difffile.DiffSource;
import com.github.katohk.tool.diff2xls.difffile.DirectFileDiffSource;
import com.github.katohk.tool.diff2xls.difffile.DirectoryDiffSource;
import com.github.katohk.tool.diff2xls.difffile.FileInputDiffSource;

/**
 * Diff2xls.java
 *
 *
 * Created: Mon Apr 12 17:33:57 2004
 *
 * @version 1.0
 */
public class Diff2xls {

    private static void usage(){
        System.err.println("Usage: Diff2xls template [-uc] [-i file.diff] [-o file.xlsx] [-e encode]");
        System.err.println("   or: Diff2xls template [-uc] --file1 path1 --file2 path2 [-o file.xlsx]");
        System.err.println("   or: Diff2xls template [-uc] --dir1 path1 --dir2 path2 [-o file.xlsx]");
        System.err.println("   or: Diff2xls template [-uc] file1 file2 [-o file.xlsx]");
        System.exit(1);
    }

    private static class Option{
        char opt = ' ';
        int mode = 3;
        int format = 0;
        String template = null;
        String fi = null;  // input file (diff file)
        String enc = "UTF-8";
        String fo = null;  // output file (xlsx)
        String file1 = null;  // for direct file comparison
        String file2 = null;  // for direct file comparison
        String dir1 = null;   // for direct directory comparison
        String dir2 = null;   // for direct directory comparison
        String args[];
        int argIndex = 0;  // track positional arguments

        Option(String[] args){
            this.args = args;
        }

        public int getMode(){
            return mode;
        }

        public String getFileNameIn(){
            return fi;
        }
        
        public String getFileNameOt(){
            return fo;
        }

        public String getTemplate(){
            return template;
        }
        
        public String getEncode() {
        	return enc;
        }
        
        public int getFormat() {
        	return format;
        }
        
        public String getFile1() {
            return file1;
        }
        
        public String getFile2() {
            return file2;
        }
        
        public String getDir1() {
            return dir1;
        }
        
        public String getDir2() {
            return dir2;
        }

        public boolean getOption() {
            for(int i=0; i<args.length; i++){
                String arg = args[i];
                if ( arg.length() > 1 && arg.charAt(0) == '-' ){
                    if (arg.startsWith("--")) {
                        // Long options
                        if (arg.equals("--file1")) {
                            if (i + 1 < args.length) {
                                file1 = args[++i];
                            } else {
                                return true;
                            }
                        } else if (arg.equals("--file2")) {
                            if (i + 1 < args.length) {
                                file2 = args[++i];
                            } else {
                                return true;
                            }
                        } else if (arg.equals("--dir1")) {
                            if (i + 1 < args.length) {
                                dir1 = args[++i];
                            } else {
                                return true;
                            }
                        } else if (arg.equals("--dir2")) {
                            if (i + 1 < args.length) {
                                dir2 = args[++i];
                            } else {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    } else {
                        // Short options
                        opt = args[i].charAt(1);
                        switch(opt){
                        case '1': mode = 1; break;
                        case '2': mode = 2; break;
                        case '3': mode = 3; break;
                        case 'u': format = 0; break;
                        case 'c': format = 1; break;
                        case 'i':
                        case 'o':
                        case 'e':
                            break;
                        default:
                            return true;
                        }
                    }
                } else {
                    switch(opt){
                    case 'i':
                        if ( fi != null ){
                            return true;
                        }
                        fi = arg;
                        break;
                    case 'e':
                        enc = arg;
                        break;
                    case 'o':
                        if ( fo != null ){
                            return true;
                        }
                        fo = arg;
                        break;
                    default:
                        // Positional argument
                        if ( template == null ){
                            template = arg;
                        } else if (file1 == null) {
                            file1 = arg;  // First positional after template
                        } else if (file2 == null) {
                            file2 = arg;  // Second positional
                        } else {
                            return true;  // Too many positional args
                        }
                        break;
                    }
                }
            }

            if ( template == null ){
                return true;
            }

            return false;
        }
        
    }
        
    public static void main(String[] args){

            Option opt = new Option(args);

            if ( opt.getOption() ){
                usage();
                System.exit(1);
            }

            try{
                DiffBlockBuilder builder = new DiffBlockBuilder();
                builder.setTemplate(opt.getTemplate());
                builder.setFileNameOt(opt.getFileNameOt());
                builder.setMode(opt.getMode());
                builder.setEncode(opt.getEncode());
                
                // Detect input mode and create appropriate DiffSource
                DiffSource diffSource = null;
                
                if (opt.getFile1() != null && opt.getFile2() != null) {
                    // Direct file comparison mode
                    File f1 = new File(opt.getFile1());
                    File f2 = new File(opt.getFile2());
                    
                    if (!f1.exists() || !f2.exists()) {
                        System.err.println("Error: One or both files do not exist");
                        System.exit(1);
                    }
                    
                    diffSource = new DirectFileDiffSource(f1, f2, 
                            Charset.forName(opt.getEncode()), opt.getFormat() == 1);
                    builder.setDiffSource(diffSource);
                    builder.setFormat(opt.getFormat());
                    
                } else if (opt.getDir1() != null && opt.getDir2() != null) {
                    // Direct directory comparison mode
                    File d1 = new File(opt.getDir1());
                    File d2 = new File(opt.getDir2());
                    
                    if (!d1.isDirectory() || !d2.isDirectory()) {
                        System.err.println("Error: One or both directories do not exist or are not directories");
                        System.exit(1);
                    }
                    
                    diffSource = new DirectoryDiffSource(d1, d2,
                            Charset.forName(opt.getEncode()), opt.getFormat() == 1);
                    builder.setDiffSource(diffSource);
                    builder.setFormat(opt.getFormat());
                    
                } else {
                    // Traditional diff input mode (file or stdin)
                    builder.setFileNameIn(opt.getFileNameIn());
                    builder.setFormat(opt.getFormat());
                }

                DiffBlockProcess diffblock = builder.getDiffBlock();
                diffblock.start();

            } catch(IOException e) {
                System.err.println(e.toString());
            }
    }
}

