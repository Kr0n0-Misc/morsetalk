package com.blueta.morsetransmitter;

import android.util.Log;
import java.util.LinkedList;

public class MosreDecisionFactor {
    final int ALPHABET_GAP_POS;
    public int[] LowerSymbolDurationData;
    public int[] LowerSymbolDurationStatisticData;
    TopPointSeaker LowerSymbolDurationStatisticTopSeaker;
    final int MAX_DATA_LEN;
    final int MAX_DATA_VAL;
    final int SYMBOL_GAP_POS;
    final int SYMBOL_LONG_POS;
    final int SYMBOL_SHORT_POS;
    PositionElement[] SymbolPosCenter;
    TopPointSeaker UpperDurationStatisticTopSeaker;
    public int[] UpperSymbolDurationData;
    public int[] UpperSymbolDurationStatisticData;
    final int WORD_GAP_POS;
    int accumulateWindowSize;
    public long duration_max;
    public long duration_min;
    PositionElement[] gabPosCenter;
    public int magnitude_max;
    public int magnitude_min;
    LinkedList<PositionElement> maxNumberThreeGroupCenter;
    LinkedList<PositionElement> maxNumberTwoGroupCenter;
    int meanWindowSize;
    LinkedList<PositionElement> possibleGapGroupCenter;
    LinkedList<PositionElement> possibleUpperSymbolGroupCenter;
    final int searchWinSize;
    public long thresholdLowerDration;
    public long thresholdSpaceDration;
    public long thresholdUpperDration;
    public int threshold_magnitude;

    class PositionElement {
        int mag;
        int pos;

        PositionElement() {
            this.mag = 0;
            this.pos = 0;
        }

        public void Clear() {
            this.mag = 0;
            this.pos = 0;
        }
    }

    class TopPointSeaker {
        int numOfSearchProve;
        private int proveSearchWindowSize;
        LinkedList<SearchProve> seachProveList;
        private int sizeOfSpace;
        int[] spaceToSearch;

        class SearchProve {
            int currPosition;
            boolean isFinsihed;
            int maxMagnitude;
            boolean seccessToFind;
            int sizeOfSpace;
            int[] space;
            int startingPoistionOfNextProve;
            int windowSize;

            SearchProve() {
            }

            public void MoveToTopPoint() {
                int scanningStartPoint;
                int scanningWindowSize;
                if (this.currPosition - (this.windowSize / 2) > 0) {
                    scanningStartPoint = this.currPosition - (this.windowSize / 2);
                } else {
                    scanningStartPoint = 0;
                }
                if (this.windowSize + scanningStartPoint <= this.sizeOfSpace) {
                    scanningWindowSize = this.windowSize;
                } else {
                    scanningWindowSize = this.sizeOfSpace - scanningStartPoint;
                }
                PositionElement maxPoistion = MosreDecisionFactor.this.getMaxInWindow(this.space, scanningStartPoint, scanningWindowSize);
                if (maxPoistion.pos == this.currPosition) {
                    this.isFinsihed = true;
                    this.seccessToFind = true;
                } else if (maxPoistion.pos == (scanningStartPoint + scanningWindowSize) - 1 && maxPoistion.mag == this.maxMagnitude) {
                    if (this.currPosition + this.windowSize <= this.sizeOfSpace) {
                        this.currPosition += this.windowSize;
                    } else {
                        this.currPosition = this.sizeOfSpace;
                    }
                    if (this.currPosition >= this.startingPoistionOfNextProve) {
                        this.isFinsihed = true;
                    }
                } else {
                    this.maxMagnitude = maxPoistion.mag;
                    this.currPosition = maxPoistion.pos;
                }
            }

            public void Init() {
                this.maxMagnitude = 0;
                this.startingPoistionOfNextProve = 0;
                this.currPosition = 0;
                this.isFinsihed = false;
                this.seccessToFind = false;
            }
        }

        TopPointSeaker(int[] space, int size_of_space) {
            this.proveSearchWindowSize = 20;
            this.numOfSearchProve = 0;
            this.seachProveList = new LinkedList();
            this.spaceToSearch = space;
            this.sizeOfSpace = size_of_space;
            this.numOfSearchProve = size_of_space / this.proveSearchWindowSize;
            for (int count = 0; count < this.numOfSearchProve; count++) {
                SearchProve searchProve = new SearchProve();
                searchProve.space = this.spaceToSearch;
                searchProve.windowSize = this.proveSearchWindowSize;
                searchProve.sizeOfSpace = this.sizeOfSpace;
                this.seachProveList.addLast(searchProve);
            }
        }

        public void Init() {
            for (int count = 0; count < this.numOfSearchProve; count++) {
                SearchProve searchProve = (SearchProve) this.seachProveList.get(count);
                searchProve.Init();
                searchProve.currPosition = (this.proveSearchWindowSize * count) + (this.proveSearchWindowSize / 2);
                if (count > 0) {
                    ((SearchProve) this.seachProveList.get(count - 1)).startingPoistionOfNextProve = searchProve.currPosition;
                }
                if (count == this.numOfSearchProve - 1) {
                    searchProve.startingPoistionOfNextProve = this.sizeOfSpace;
                }
            }
        }

        public void DoSearch() {
            int dbg_count = 0;
            int numOfNotFinished;
            do {
                numOfNotFinished = 0;
                for (int count = 0; count < this.seachProveList.size(); count++) {
                    SearchProve prove = (SearchProve) this.seachProveList.get(count);
                    if (!prove.isFinsihed) {
                        prove.MoveToTopPoint();
                    }
                    if (!prove.isFinsihed) {
                        numOfNotFinished++;
                    }
                }
                dbg_count++;
                if (dbg_count > 20) {
                    Log.d("PROVE", "woops!!!! something wrong");
                    continue;
                }
            } while (numOfNotFinished != 0);
        }

        private LinkedList<PositionElement> getTopPointElement() {
            int count;
            LinkedList<PositionElement> topPointList = new LinkedList();
            LinkedList<PositionElement> topPointNoDuplicatedList = new LinkedList();
            for (count = 0; count < this.seachProveList.size(); count++) {
                if (((SearchProve) this.seachProveList.get(count)).seccessToFind) {
                    PositionElement topElement = new PositionElement();
                    topElement.Clear();
                    topElement.mag = ((SearchProve) this.seachProveList.get(count)).maxMagnitude;
                    topElement.pos = ((SearchProve) this.seachProveList.get(count)).currPosition;
                    topPointList.add(topElement);
                }
            }
            for (count = 0; count < topPointList.size(); count++) {
                boolean isDuplicated = false;
                for (int count_2 = 0; count_2 < topPointNoDuplicatedList.size(); count_2++) {
                    if (((PositionElement) topPointList.get(count)).pos == ((PositionElement) topPointNoDuplicatedList.get(count_2)).pos) {
                        isDuplicated = true;
                    }
                }
                if (!isDuplicated) {
                    topPointNoDuplicatedList.add((PositionElement) topPointList.get(count));
                }
            }
            return topPointNoDuplicatedList;
        }
    }

    public MosreDecisionFactor() {
        this.MAX_DATA_LEN = 1000;
        this.MAX_DATA_VAL = 256;
        this.searchWinSize = 10;
        this.SYMBOL_GAP_POS = 0;
        this.ALPHABET_GAP_POS = 1;
        this.WORD_GAP_POS = 2;
        this.SYMBOL_SHORT_POS = 0;
        this.SYMBOL_LONG_POS = 1;
        this.magnitude_min = 0;
        this.magnitude_max = 0;
        this.duration_min = 0;
        this.duration_max = 0;
        this.threshold_magnitude = 0;
        this.thresholdUpperDration = 0;
        this.thresholdLowerDration = 0;
        this.thresholdSpaceDration = 0;
        this.LowerSymbolDurationData = new int[1000];
        this.LowerSymbolDurationStatisticData = new int[1000];
        this.UpperSymbolDurationData = new int[1000];
        this.UpperSymbolDurationStatisticData = new int[1000];
        this.gabPosCenter = new PositionElement[3];
        this.SymbolPosCenter = new PositionElement[2];
        this.maxNumberThreeGroupCenter = new LinkedList();
        this.maxNumberTwoGroupCenter = new LinkedList();
        this.LowerSymbolDurationStatisticTopSeaker = new TopPointSeaker(this.LowerSymbolDurationStatisticData, 1000);
        this.UpperDurationStatisticTopSeaker = new TopPointSeaker(this.UpperSymbolDurationStatisticData, 1000);
        this.accumulateWindowSize = 10;
        this.meanWindowSize = 10;
    }

    public void ResetFactors() {
        int cnt;
        this.magnitude_min = 0;
        this.magnitude_max = 0;
        this.duration_min = 0;
        this.duration_max = 0;
        this.threshold_magnitude = 0;
        this.thresholdUpperDration = 400;
        this.thresholdLowerDration = 400;
        this.thresholdSpaceDration = 1000;
        for (cnt = 0; cnt < 1000; cnt++) {
            this.LowerSymbolDurationData[cnt] = 0;
            this.UpperSymbolDurationData[cnt] = 0;
            this.LowerSymbolDurationStatisticData[cnt] = 0;
            this.UpperSymbolDurationStatisticData[cnt] = 0;
        }
        for (cnt = 0; cnt < this.gabPosCenter.length; cnt++) {
            this.gabPosCenter[cnt] = null;
        }
        for (cnt = 0; cnt < this.SymbolPosCenter.length; cnt++) {
            this.SymbolPosCenter[cnt] = null;
        }
    }

    public void AddLowerSymbolDurationDataInMillicsec(double data) {
        if (this.gabPosCenter[0] == null || this.gabPosCenter[0].mag <= 100) {
            int durationVal = ((int) data) / 10;
            if (durationVal < 1000) {
                int[] iArr = this.LowerSymbolDurationData;
                iArr[durationVal] = iArr[durationVal] + 1;
            }
            calSymbolDurationSumInWindow(this.LowerSymbolDurationData, this.LowerSymbolDurationStatisticData);
            this.LowerSymbolDurationStatisticTopSeaker.Init();
            this.LowerSymbolDurationStatisticTopSeaker.DoSearch();
            calLowerSymbolDurationsPosition();
            calLowerSymbolDecisionThreshold();
        }
    }

    private void calSymbolDurationSumInWindow(int[] srcData, int[] dstData) {
        for (int count_1 = 0; count_1 < 1000; count_1++) {
            int summation = 0;
            for (int count_2 = 0; count_2 < this.accumulateWindowSize; count_2++) {
                int point;
                if (this.accumulateWindowSize / 2 > count_1) {
                    point = count_1 + count_2;
                } else {
                    point = (count_1 - (this.accumulateWindowSize / 2)) + count_2;
                }
                if (point >= 1000) {
                    break;
                }
                summation += srcData[point];
            }
            dstData[count_1] = summation;
        }
    }

    private void calSymbolDurationMeanInWindow(int[] srcData, int[] dstData) {
        for (int count_1 = 0; count_1 < 1000; count_1++) {
            int summation = 0;
            for (int count_2 = 0; count_2 < this.accumulateWindowSize; count_2++) {
                int point;
                if (this.accumulateWindowSize / 2 > count_1) {
                    point = count_1 + count_2;
                } else {
                    point = (count_1 - (this.accumulateWindowSize / 2)) + count_2;
                }
                if (point >= 1000) {
                    break;
                }
                summation += srcData[point];
            }
            dstData[count_1] = summation / this.meanWindowSize;
        }
    }

    private void calLowerSymbolDurationsPosition() {
        int count;
        this.possibleGapGroupCenter = this.LowerSymbolDurationStatisticTopSeaker.getTopPointElement();
        this.maxNumberThreeGroupCenter.clear();
        if (this.possibleGapGroupCenter.size() > 0) {
            int numOfMax = 0;
            do {
                int maxPos = 0;
                for (count = 0; count < this.possibleGapGroupCenter.size() - 1; count++) {
                    if (((PositionElement) this.possibleGapGroupCenter.get(maxPos)).mag < ((PositionElement) this.possibleGapGroupCenter.get(count + 1)).mag) {
                        maxPos = count + 1;
                    }
                }
                this.maxNumberThreeGroupCenter.add((PositionElement) this.possibleGapGroupCenter.get(maxPos));
                this.possibleGapGroupCenter.remove(maxPos);
                if (this.possibleGapGroupCenter.size() == 1) {
                    this.maxNumberThreeGroupCenter.add((PositionElement) this.possibleGapGroupCenter.get(0));
                    this.possibleGapGroupCenter.remove(0);
                }
                numOfMax++;
                if (this.possibleGapGroupCenter.size() == 0) {
                    break;
                }
            } while (numOfMax <= 3);
        }
        if (this.maxNumberThreeGroupCenter.size() > 0) {
            int gabKind = 0;
            do {
                int minPos = 0;
                for (count = 0; count < this.maxNumberThreeGroupCenter.size() - 1; count++) {
                    if (((PositionElement) this.maxNumberThreeGroupCenter.get(minPos)).pos > ((PositionElement) this.maxNumberThreeGroupCenter.get(count + 1)).pos) {
                        minPos = count + 1;
                    }
                }
                this.gabPosCenter[gabKind] = (PositionElement) this.maxNumberThreeGroupCenter.get(minPos);
                this.maxNumberThreeGroupCenter.remove(minPos);
                gabKind++;
                if (this.maxNumberThreeGroupCenter.size() == 0) {
                    return;
                }
            } while (gabKind < 3);
        }
    }

    private void calLowerSymbolDecisionThreshold() {
        if (!(this.gabPosCenter[0] == null || this.gabPosCenter[1] == null)) {
            this.thresholdLowerDration = (long) (((this.gabPosCenter[0].pos + this.gabPosCenter[1].pos) / 2) * 10);
        }
        if (this.gabPosCenter[1] == null || this.gabPosCenter[2] == null) {
            this.thresholdSpaceDration = (long) ((int) (((double) this.thresholdLowerDration) * 2.5d));
        } else {
            this.thresholdSpaceDration = (long) ((int) (((double) this.thresholdLowerDration) * 2.5d));
        }
    }

    public void AddUpperSymbolDurationDataInMillicsec(double data) {
        int durationVal = ((int) data) / 10;
        if (durationVal < 1000) {
            int[] iArr = this.UpperSymbolDurationData;
            iArr[durationVal] = iArr[durationVal] + 1;
        }
        calSymbolDurationSumInWindow(this.UpperSymbolDurationData, this.UpperSymbolDurationStatisticData);
        this.UpperDurationStatisticTopSeaker.Init();
        this.UpperDurationStatisticTopSeaker.DoSearch();
        calUpperSymbolDurationsPosition();
        calUpperSymbolDecisionThreshold();
    }

    private void calUpperSymbolDurationsPosition() {
        int count;
        this.possibleUpperSymbolGroupCenter = this.UpperDurationStatisticTopSeaker.getTopPointElement();
        this.maxNumberTwoGroupCenter.clear();
        if (this.possibleUpperSymbolGroupCenter.size() > 0) {
            int numOfMax = 0;
            do {
                int maxPos = 0;
                for (count = 0; count < this.possibleUpperSymbolGroupCenter.size() - 1; count++) {
                    if (((PositionElement) this.possibleUpperSymbolGroupCenter.get(maxPos)).mag < ((PositionElement) this.possibleUpperSymbolGroupCenter.get(count + 1)).mag) {
                        maxPos = count + 1;
                    }
                }
                this.maxNumberTwoGroupCenter.add((PositionElement) this.possibleUpperSymbolGroupCenter.get(maxPos));
                this.possibleUpperSymbolGroupCenter.remove(maxPos);
                if (this.possibleUpperSymbolGroupCenter.size() == 1) {
                    this.maxNumberTwoGroupCenter.add((PositionElement) this.possibleUpperSymbolGroupCenter.get(0));
                    this.possibleUpperSymbolGroupCenter.remove(0);
                }
                numOfMax++;
                if (this.possibleUpperSymbolGroupCenter.size() == 0) {
                    break;
                }
            } while (numOfMax <= 3);
        }
        if (this.maxNumberTwoGroupCenter.size() > 0) {
            int UpperSymbolKind = 0;
            do {
                int minPos = 0;
                for (count = 0; count < this.maxNumberTwoGroupCenter.size() - 1; count++) {
                    if (((PositionElement) this.maxNumberTwoGroupCenter.get(minPos)).pos > ((PositionElement) this.maxNumberTwoGroupCenter.get(count + 1)).pos) {
                        minPos = count + 1;
                    }
                }
                this.SymbolPosCenter[UpperSymbolKind] = (PositionElement) this.maxNumberTwoGroupCenter.get(minPos);
                this.maxNumberTwoGroupCenter.remove(minPos);
                UpperSymbolKind++;
                if (this.maxNumberTwoGroupCenter.size() == 0) {
                    return;
                }
            } while (UpperSymbolKind < 2);
        }
    }

    private void calUpperSymbolDecisionThreshold() {
        if (this.SymbolPosCenter[0] != null && this.SymbolPosCenter[1] != null) {
            this.thresholdUpperDration = (long) (((this.SymbolPosCenter[0].pos + this.SymbolPosCenter[1].pos) / 2) * 10);
        }
    }

    private PositionElement getMaxInWindow(int[] data, int startPoint, int windowSize) {
        PositionElement maxVal = new PositionElement();
        maxVal.Clear();
        maxVal.pos = startPoint;
        for (int count = startPoint; count < startPoint + windowSize; count++) {
            if (maxVal.mag <= data[count]) {
                maxVal.mag = data[count];
                maxVal.pos = count;
            }
        }
        return maxVal;
    }
}
