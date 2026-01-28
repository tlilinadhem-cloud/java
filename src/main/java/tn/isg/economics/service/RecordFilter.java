package tn.isg.economics.service;

import tn.isg.economics.model.ExportRecord;

/**
 * Custom functional interface for filtering export records.
 */
@FunctionalInterface
public interface RecordFilter {
    boolean test(ExportRecord record);
}

