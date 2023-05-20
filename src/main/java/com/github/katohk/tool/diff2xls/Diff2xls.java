package com.github.katohk.tool.diff2xls;

import java.io.IOException;

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
        System.err.println
            ("Usage: Diff2xls template [-uc] [-i file.diff -o file.xls -e encode]");
        System.exit(1);
    }

    private static class Option{
        char opt = ' ';
        int mode = 3;
        int format = 0;
        String template = null;
        String fi = null;
        String enc = "UTF-8";
        String fo = null;
        String args[];

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

        public boolean getOption() {
            for(int i=0; i<args.length; i++){
                String arg = args[i];
                if ( arg.length() > 1 && arg.charAt(0) == '-' ){
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
                }else{
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
                        if ( template != null ){
                            return true;
                        }
                        template = new String(arg);
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
                builder.setFileNameIn(opt.getFileNameIn());
                builder.setFileNameOt(opt.getFileNameOt());
                builder.setMode(opt.getMode());
                builder.setEncode(opt.getEncode());
                builder.setFormat(opt.getFormat());

                DiffBlockProcess diffblock = builder.getDiffBlock();
                diffblock.start();

            } catch(IOException e) {
                System.err.println(e.toString());
            }
    }
}
