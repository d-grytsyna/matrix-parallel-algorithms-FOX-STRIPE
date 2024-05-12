package org.matrixparallel;

public class Service {

    public static void multiplyMatrixStripeParallel(int rowIndex, Matrix m1, Matrix m2, int numThreads, Matrix resultMatrix){
        int endIndex;
        if(rowIndex<numThreads-1) endIndex = (rowIndex+1)*(m1.getRows()/numThreads);
        else endIndex = m1.getRows();
        for(int j=rowIndex*(m1.getRows()/numThreads); j<endIndex; j++){
            int startIndex = j;
            Integer[] matrixRow = m1.getRow(startIndex);
            for(int k=startIndex, counter = 0; counter<m2.getCols(); counter++ ){
                Integer[] matrixCol = m2.getCol(k);
                Integer resultElement = multiplyRowByColumn(matrixRow, matrixCol);
                resultMatrix.setElement(startIndex, k, resultElement);
                k--;
                if(k<0) k = m2.getCols()-1;
            }
        }
    }
    public static void multiplyMatrixFox(MatrixBlock m1, MatrixBlock m2, MatrixBlock resultMatrix, int threadIndex, int q){
        int resultRow;
        int resultCol;
        if(threadIndex<q){
            resultRow = 0;
            resultCol = threadIndex;
        }else{
            resultRow = threadIndex/q;
            resultCol = threadIndex%q;
        }
        int i = resultRow;
        int i2 = resultRow;
        int j = resultCol;
        for(int c=0; c<q; c++){
            Matrix a = m1.getMatrix(i, i2);
            Matrix b = m2.getMatrix(i2, j);
            Matrix subResult = multiplyMatrix(a,b);
            resultMatrix.addElements(resultRow, resultCol, subResult);
            i2++;
            if(i2>=q) i2 = 0;
        }
    }

    public static Matrix multiplyMatrix(Matrix m1, Matrix m2){
        Matrix resultMatrix = new Matrix(m1.getRows(), m1.getCols());
        for(int i=0; i<m1.getRows(); i++){
            for(int j=0; j<m2.getCols(); j++){
                resultMatrix.setElement(i, j, multiplyRowByColumn(m1.getRow(i), m2.getCol(j)));
            }
        }
        return resultMatrix;
    }
    private static Integer multiplyRowByColumn(Integer[] row, Integer[] column){
        Integer result = 0;
        for(int i=0; i<row.length; i++){
            result += row[i] * column[i];
        }
        return result;
    }

    public static Boolean matrixEqual(Matrix m1, Matrix m2){
        for(int i=0; i<m1.getRows(); i++){
            for(int j=0; j<m1.getCols(); j++){
                if(m1.getElement(i,j).intValue()!=m2.getElement(i,j).intValue()) return false;
            }
        }
        return true;
    }
}
