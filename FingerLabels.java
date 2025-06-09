public enum FingerLabels {
    Pinky, Ring, Middle, Index, Thumb, Unknown;
    public FingerLabels getNext() {
        int nextIndex = ordinal() + 1;
        if (nextIndex >= FingerLabels.values().length) {
            nextIndex = 0;
        }
        return FingerLabels.values()[nextIndex];
    }

    public FingerLabels getPrev() {
        int prevIndex = ordinal() - 1;
        if (prevIndex < 0) {
            prevIndex = values().length-1;

        }
        return FingerLabels.values()[prevIndex];
    }

}
