package tn.isg.economics.repository;

import lombok.Getter;
import tn.isg.economics.annotations.Audit;
import tn.isg.economics.model.ExportRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Simple in-memory repository for demos and tests.
 */
@Audit(owner = "dashboard")
public class InMemoryExportRecordRepository implements ExportRecordRepository {

    @Getter
    private final List<ExportRecord> storage = new ArrayList<>();

    @Override
    public List<ExportRecord> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public void saveAll(List<ExportRecord> records) {
        storage.clear();
        storage.addAll(records);
        storage.sort(Comparator.comparing(ExportRecord::date));
    }

    @Override
    public Optional<ExportRecord> findLatest() {
        return storage.stream().max(Comparator.comparing(ExportRecord::date));
    }
}

