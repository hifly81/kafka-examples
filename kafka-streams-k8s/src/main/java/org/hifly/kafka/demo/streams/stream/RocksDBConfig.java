package org.hifly.kafka.demo.streams.stream;

import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.*;

import java.util.Map;

public class RocksDBConfig implements RocksDBConfigSetter {

    static {
        RocksDB.loadLibrary();
    }

    private static final long TOTAL_OFF_HEAP_MEMORY = 1024 * 1024 * 100; // 100 MB
    private static final double INDEX_FILTER_BLOCK_RATIO = 0.2; // 20% of cache dedicated to indexes & filter
    private static final long TOTAL_MEMTABLE_MEMORY = 1024 * 1024 * 50; // 50 MB
    private static final int BLOCK_SIZE = 4096; // 4KB
    private static final int N_MEMTABLES = 3;
    private static final int MEMTABLE_SIZE = 1024 * 1024 * 15; // 15 MB

    private final static org.rocksdb.Cache cache = new org.rocksdb.LRUCache(TOTAL_OFF_HEAP_MEMORY, -1, false, INDEX_FILTER_BLOCK_RATIO);
    private final static org.rocksdb.WriteBufferManager writeBufferManager = new org.rocksdb.WriteBufferManager(TOTAL_MEMTABLE_MEMORY, cache);


    @Override
    public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {

        BlockBasedTableConfig tableConfig = (BlockBasedTableConfig) options.tableFormatConfig();

        // These three options in combination will limit the memory used by RocksDB to the size passed to the block cache (TOTAL_OFF_HEAP_MEMORY)
        tableConfig.setBlockCache(cache);
        tableConfig.setCacheIndexAndFilterBlocks(true);
        options.setWriteBufferManager(writeBufferManager);

        // These options are recommended to be set when bounding the total memory
        tableConfig.setCacheIndexAndFilterBlocksWithHighPriority(true);
        tableConfig.setPinTopLevelIndexAndFilter(true);
        tableConfig.setBlockSize(BLOCK_SIZE);

        options.setMaxWriteBufferNumber(N_MEMTABLES);
        options.setWriteBufferSize(MEMTABLE_SIZE);
        // Compression will trade some cpu usage increase vs lower storage usage
        options.setCompressionType(CompressionType.LZ4_COMPRESSION);
        
        //Compaction Style
        options.setCompactionStyle(CompactionStyle.UNIVERSAL);
        // To enable optin for read
        options.setCompactionStyle(CompactionStyle.LEVEL);

        options.setTableFormatConfig(tableConfig);
    }

    @Override
    public void close(final String storeName, final Options options) {
        // Cache and WriteBufferManager should not be closed here, as the same objects
        // are shared by every store instance.
    }
}
