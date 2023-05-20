package com.github.katohk.tool.diff2xls.difffile;

import java.io.IOException;

public interface DiffBlockIF {

   public String getLeftName();
   public String getRightName();

   public DiffLine getLeftDiffLine();
   public DiffLine getRightDiffLine();
   
   public DiffBlockState getDiffEntry() throws IOException;
   public boolean isEmpty();
}
