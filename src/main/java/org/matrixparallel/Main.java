package org.matrixparallel;

public class Main {
    public static void main(String[] args) {
        int size = 1000;
        int q = 3;

        Matrix A = new Matrix(size, size);
        A.generateMatrix();
        A.writeToFile("A");


        Matrix B = new Matrix(size, size);
        B.generateMatrix();
        B.writeToFile("B");


        //SEQUENTIAL
        Matrix sequential = calculateSequential(A, B);

        //FOX
        Matrix fox = calculateFOX(A, B, q);

        //STRIPE
        Matrix stripe = calculateStripe(A, B, 8);


        //COMPARE RESULTS
        System.out.println("Results equal fox & sequential: " +Service.matrixEqual(fox, sequential));
        System.out.println("Results equal stripe & sequential: " +Service.matrixEqual(stripe, sequential));
        System.out.println("Results equal fox & stripe: " +Service.matrixEqual(fox, stripe));




    }

    private static Matrix calculateFOX(Matrix A, Matrix B, int q){
        //Calculate number of threads needed
        int numThreads = q * q;

        //Split Matrix A into blocks
        MatrixBlock matrixBlockA = new MatrixBlock(q);
        matrixBlockA.setBlocks(A);

        //Split Matrix B into blocks
        MatrixBlock matrixBlockB = new MatrixBlock(q);
        matrixBlockB.setBlocks(B);

        //Set empty blocks in result matrix
        MatrixBlock resultBlocks = new MatrixBlock(q);
        resultBlocks.setEmptyBlocks(A);

        //Start time
        long startTime = System.currentTimeMillis();

        //Calculation
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int startIndex = i;
            threads[i] = new Thread(() -> {
                // Multiply Blocks
                Service.multiplyMatrixFox(matrixBlockA, matrixBlockB, resultBlocks, startIndex, q);
            });
            threads[i].start();
        }

        //Wait till all threads complete calculations
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //End time
        long endTime = System.currentTimeMillis();

        //Elapsed
        long elapsedTime = endTime - startTime;
        System.out.println("Parallel computing using fox algorithm, elapsed time: " + elapsedTime);

//        resultBlocks.printMatrix();

        // Revert blocks back to matrix to return as result
        Matrix finalResult  = resultBlocks.blocksToMatrix(q);
//
//        System.out.println("FOX FINAL RESULT ");
//
//        finalResult.printMatrix();

        return finalResult;
    }

    private static Matrix calculateStripe(Matrix A, Matrix B, int numThreads){
        Matrix finalResult = new Matrix(A.getRows(), A.getCols());

        //Set threads
        Thread[] threads = new Thread[numThreads];


        //Start time
        long startTime = System.currentTimeMillis();

        //Calculation
        for (int i = 0; i < numThreads; i++) {
            int startIndex = i;
            threads[i] = new Thread(() -> {
                // Each thread multiplies amount of rows starting from index
                Service.multiplyMatrixStripeParallel(startIndex, A, B, numThreads,finalResult);
            });
            threads[i].start();
        }
        //Wait till all threads complete calculations
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //End time
        long endTime = System.currentTimeMillis();

        //Elapsed
        long elapsedTime = endTime - startTime;
        System.out.println("Parallel computing using stripe algorithm, elapsed time: " + elapsedTime);

//        System.out.println("STRIPE FINAL RESULT ");
//        finalResult.printMatrix();
        return finalResult;
    }

    private static Matrix calculateSequential(Matrix A, Matrix B){
        //Start time
        long startTime = System.currentTimeMillis();

        //Calculation
        Matrix finalResult = Service.multiplyMatrix(A, B);

        //End time
        long endTime = System.currentTimeMillis();

        //Elapsed
        long elapsedTime = endTime - startTime;
        System.out.println("Sequential computing elapsed time: " + elapsedTime);
        return finalResult;
    }


}