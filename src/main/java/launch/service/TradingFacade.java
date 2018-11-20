package launch.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import launch.domain.BestBuy;
import launch.domain.File;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TradingFacade implements Consumer<File> {

    private final AtomicLong taskIdGenerator = new AtomicLong();

    private final BlockingQueue<BestBuyCalculateTask> submittedTasksQueue = new LinkedBlockingQueue<>();

    private final ZipReader zipReader;

    private volatile boolean isDone = false;

    public TradingFacade() {
        this.zipReader = new ZipReader();
    }

    public String getBestBuysFor(InputStream zipArchive) {
        zipReader.registerConsumer(this);
        zipReader.readZipArchive(zipArchive);
        List<BestBuyCalculateTask> finishedTasks = spinUntilAllTasksAreDone();
        finishedTasks.sort(Comparator.comparingLong(bestBuyCalculateTask -> bestBuyCalculateTask.id));
        return makeBestBuysResponse(finishedTasks);
    }

    private List<BestBuyCalculateTask> spinUntilAllTasksAreDone() {
        while (!isDone) {
            Thread.yield();
        }
        int totalTasks = (int) taskIdGenerator.get();
        List<BestBuyCalculateTask> finishedTasks = new ArrayList<>(totalTasks);
        for (int i = 0; i < totalTasks; ++i) {
            BestBuyCalculateTask queueElement = getQueueElement();
            if (queueElement != null)
                finishedTasks.add(queueElement);
        }
        return finishedTasks;
    }

    private BestBuyCalculateTask getQueueElement() {
        try {
            return submittedTasksQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    private String makeBestBuysResponse(final List<BestBuyCalculateTask> results) {
        StringBuilder jsonResult = new StringBuilder();
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        for (BestBuyCalculateTask task : results) {
            BestBuy bestBuy = task.getResult();
            if (bestBuy != null) {
                objectNode.set(bestBuy.getBatchName(), JsonNodeFactory.instance.objectNode()
                        .put("buyPoint", bestBuy.getBuyPoint().toString())
                        .put("sellPoint", bestBuy.getSellPoint().toString()));
            }
        }
        jsonResult.append(objectNode.toString());
        return jsonResult.toString();
    }

    @Override
    public void accept(final File file) {
        if (file == null) {
            isDone = true;
        } else {
            ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            forkJoinPool.submit(new BestBuyCalculateTask(taskIdGenerator.incrementAndGet(), file));
        }
    }

    private class BestBuyCalculateTask implements Runnable {

        private final long id;

        private final File file;

        private volatile BestBuy result;

        public BestBuyCalculateTask(final long id, final File file) {
            this.id = id;
            this.file = file;
        }

        public long getId() {
            return id;
        }

        public boolean isDone() {
            return result != null;
        }

        public BestBuy getResult() {
            return result;
        }

        @Override
        public void run() {
            processImageFile(file);
        }

        private void processImageFile(final File file) {
            try {
                tryProcessImageFile(file);
            } catch (Exception e) {
                //skipping file
            }
        }

        private void tryProcessImageFile(final File file) {
            String imageContent = QRCodeReader.decodeQRCode(file.getContentStream());
            double[] stocksPrices = StockParser.getStocks(imageContent);
            if (stocksPrices.length != 0) {
                result = BestBuyCalculator.calculateBestBuy(stocksPrices, file.getFileName());
                submittedTasksQueue.add(this);
            }
        }
    }
}
