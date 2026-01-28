package tn.isg.economics.repository;

import tn.isg.economics.model.ExportRecord;

import java.util.List;
import java.util.Optional;

/**
 * Data access contract.
 */
public interface ExportRecordRepository {
    List<ExportRecord> findAll();

    void saveAll(List<ExportRecord> records);

    Optional<ExportRecord> findLatest();
}

