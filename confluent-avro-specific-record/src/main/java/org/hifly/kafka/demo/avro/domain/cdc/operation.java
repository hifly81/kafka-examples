/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.hifly.kafka.demo.avro.domain.cdc;
@org.apache.avro.specific.AvroGenerated
public enum operation implements org.apache.avro.generic.GenericEnumSymbol<operation> {
  INSERT, UPDATE, DELETE, REFRESH  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"operation\",\"namespace\":\"org.hifly.kafka.demo.avro.domain.cdc\",\"symbols\":[\"INSERT\",\"UPDATE\",\"DELETE\",\"REFRESH\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
