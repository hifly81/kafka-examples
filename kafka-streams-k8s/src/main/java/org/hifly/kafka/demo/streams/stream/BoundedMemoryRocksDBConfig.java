package org.hifly.kafka.demo.streams.stream;

import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;

import java.util.Map;

public class BoundedMemoryRocksDBConfig implements RocksDBConfigSetter {

  static {
    // jemalloc
    RocksDB.loadLibrary();
  }

  private static final long TOTAL_OFF_HEAP_MEMORY = 1024 * 1024 * 1024L; // 1 GB
  private static final long TOTAL_MEMTABLE_MEMORY = 128 * 1024 * 1024L; // 128 MB
  private static final double INDEX_FILTER_BLOCK_RATIO = 0.2; // 20% of block cache used for filters
  private static final long BLOCK_SIZE = 4 * 1024L; // 4 KB
  private static final int N_MEMTABLES = 2;
  private static final long MEMTABLE_SIZE = 64 * 1024 * 1024L; // 64 MB


  // See #1 below
  private static org.rocksdb.Cache cache =
          new org.rocksdb.LRUCache(
                  TOTAL_OFF_HEAP_MEMORY, -1, true, INDEX_FILTER_BLOCK_RATIO);

  private static org.rocksdb.WriteBufferManager writeBufferManager =
          new org.rocksdb.WriteBufferManager(TOTAL_MEMTABLE_MEMORY, cache);

  @Override
  public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {

    BlockBasedTableConfig tableConfig = (BlockBasedTableConfig) options.tableFormatConfig();

    // These three options in combination will limit the memory used by RocksDB to the size passed to the block cache (TOTAL_OFF_HEAP_MEMORY)
    tableConfig.setBlockCache(cache);
    tableConfig.setCacheIndexAndFilterBlocks(true);
    options.setWriteBufferManager(writeBufferManager);

    // These options are recommended to be set when bounding the total memory
    // See #2 below
    tableConfig.setCacheIndexAndFilterBlocksWithHighPriority(true);
    tableConfig.setPinTopLevelIndexAndFilter(true);
    // See #3 below
    tableConfig.setBlockSize(BLOCK_SIZE);
    options.setMaxWriteBufferNumber(N_MEMTABLES);
    options.setWriteBufferSize(MEMTABLE_SIZE);
    // Enable compression (optional). Compression can decrease the required storage
    // and increase the CPU usage of the machine. For CompressionType values, see
    // https://javadoc.io/static/org.rocksdb/rocksdbjni/6.4.6/org/rocksdb/CompressionType.html.
    options.setCompressionType(CompressionType.LZ4_COMPRESSION);

    options.setTableFormatConfig(tableConfig);
  }

  @Override
  public void close(final String storeName, final Options options) {
    // Cache and WriteBufferManager should not be closed here, as the same objects are shared by every store instance.
  }


}