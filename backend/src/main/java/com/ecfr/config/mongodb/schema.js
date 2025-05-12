// MongoDB Schema for eCFR XML Documents

// Base Schema with common fields
const baseSchema = {
  id: { type: String, required: true, pattern: '^[A-Za-z0-9-_]+$' },
  type: { type: String, required: true, pattern: '^[A-Za-z0-9-_]+$' },
  n: { type: String, pattern: '^[A-Za-z0-9-_]+$' },
  level: { type: String, pattern: '^[A-Za-z0-9-_]+$' },
  label: { type: String, pattern: '^[A-Za-z0-9-_]+$' },
  title: String,
  effectiveDate: { type: String, pattern: '^\\d{4}-\\d{2}-\\d{2}$' },
  amendmentDate: { type: String, pattern: '^\\d{4}-\\d{2}-\\d{2}$' },
  nodeType: { type: String, required: true }
};

// Division Schema
const divisionSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  level: { type: Number, required: true, min: 1, max: 9 },
  type: String,
  source: String,
  subDivisions: [{ type: Object, ref: 'Division' }],
  paragraphs: [{ type: Object, ref: 'Paragraph' }],
  tables: [{ type: Object, ref: 'Table' }],
  appendices: [{ type: Object, ref: 'Appendix' }],
  notes: [{ type: Object, ref: 'Note' }],
  graphics: [{ type: Object, ref: 'Graphic' }],
  authorities: [{ type: Object, ref: 'Authority' }],
  reserved: [{ type: Object, ref: 'Reserved' }],
  extracts: [{ type: Object, ref: 'Extract' }]
};

// Table Schema
const tableSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  border: { type: Number, min: 0 },
  cellPadding: { type: Number, min: 0 },
  cellSpacing: { type: Number, min: 0 },
  width: String,
  align: String,
  caption: String,
  headers: [{ type: Object, ref: 'TableHeader' }],
  rows: [{ type: Object, ref: 'TableRow' }],
  footnotes: [{ type: Object, ref: 'Footnote' }],
  source: String
};

// Table Header Schema
const tableHeaderSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  scope: String,
  align: String
};

// Table Row Schema
const tableRowSchema = {
  ...baseSchema,
  cells: [{ type: Object, ref: 'TableCell', required: true }],
  align: String,
  verticalAlign: String
};

// Table Cell Schema
const tableCellSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  align: String,
  verticalAlign: String,
  colspan: Number,
  rowspan: Number
};

// Graphic Schema
const graphicSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  type: String,
  sourceUrl: { type: String, pattern: '^https?://.*' },
  pdfUrl: { type: String, pattern: '^https?://.*\\.pdf$' },
  width: String,
  height: String,
  altText: String
};

// Paragraph Schema
const paragraphSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  type: { type: String, pattern: '^P(-[1-9])?$' },
  level: Number,
  align: String,
  citations: [{ type: Object, ref: 'Citation' }],
  crossReferences: [{ type: Object, ref: 'CrossReference' }],
  footnotes: [{ type: Object, ref: 'Footnote' }],
  source: String,
  text: String,
  ellipses: [{ type: Object, ref: 'Ellipsis' }],
  notes: [{ type: Object, ref: 'Note' }],
  tables: [{ type: Object, ref: 'Table' }],
  graphics: [{ type: Object, ref: 'Graphic' }],
  authorities: [{ type: Object, ref: 'Authority' }],
  reserved: [{ type: Object, ref: 'Reserved' }],
  extracts: [{ type: Object, ref: 'Extract' }]
};

// Extract Schema
const extractSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  type: String,
  paragraphs: [{ type: Object, ref: 'Paragraph' }],
  divisions: [{ type: Object, ref: 'Division' }],
  tables: [{ type: Object, ref: 'Table' }],
  graphics: [{ type: Object, ref: 'Graphic' }],
  notes: [{ type: Object, ref: 'Note' }],
  citations: [{ type: Object, ref: 'Citation' }],
  crossReferences: [{ type: Object, ref: 'CrossReference' }],
  source: String
};

// Authority Schema
const authoritySchema = {
  ...baseSchema,
  content: { type: String, required: true },
  type: String,
  level: Number,
  subAuthorities: [{ type: Object, ref: 'Authority' }],
  citations: [{ type: Object, ref: 'Citation' }],
  crossReferences: [{ type: Object, ref: 'CrossReference' }],
  notes: [{ type: Object, ref: 'Note' }],
  source: String
};

// Source Schema
const sourceSchema = {
  ...baseSchema,
  content: { type: String, required: true },
  type: String,
  level: Number,
  subSources: [{ type: Object, ref: 'Source' }],
  citations: [{ type: Object, ref: 'Citation' }],
  crossReferences: [{ type: Object, ref: 'CrossReference' }],
  notes: [{ type: Object, ref: 'Note' }],
  effectiveDate: { type: String, pattern: '^\\d{4}-\\d{2}-\\d{2}$' },
  amendmentDate: { type: String, pattern: '^\\d{4}-\\d{2}-\\d{2}$' }
};

// Create collections with their respective schemas
db.createCollection('divisions', { validator: { $jsonSchema: { bsonType: 'object', properties: divisionSchema } } });
db.createCollection('tables', { validator: { $jsonSchema: { bsonType: 'object', properties: tableSchema } } });
db.createCollection('tableHeaders', { validator: { $jsonSchema: { bsonType: 'object', properties: tableHeaderSchema } } });
db.createCollection('tableRows', { validator: { $jsonSchema: { bsonType: 'object', properties: tableRowSchema } } });
db.createCollection('tableCells', { validator: { $jsonSchema: { bsonType: 'object', properties: tableCellSchema } } });
db.createCollection('graphics', { validator: { $jsonSchema: { bsonType: 'object', properties: graphicSchema } } });
db.createCollection('paragraphs', { validator: { $jsonSchema: { bsonType: 'object', properties: paragraphSchema } } });
db.createCollection('extracts', { validator: { $jsonSchema: { bsonType: 'object', properties: extractSchema } } });
db.createCollection('authorities', { validator: { $jsonSchema: { bsonType: 'object', properties: authoritySchema } } });
db.createCollection('sources', { validator: { $jsonSchema: { bsonType: 'object', properties: sourceSchema } } });

// Create indexes for frequently queried fields
db.divisions.createIndex({ id: 1 }, { unique: true });
db.divisions.createIndex({ level: 1 });
db.divisions.createIndex({ type: 1 });

db.tables.createIndex({ id: 1 }, { unique: true });
db.tables.createIndex({ type: 1 });

db.paragraphs.createIndex({ id: 1 }, { unique: true });
db.paragraphs.createIndex({ type: 1 });
db.paragraphs.createIndex({ level: 1 });

db.authorities.createIndex({ id: 1 }, { unique: true });
db.authorities.createIndex({ type: 1 });
db.authorities.createIndex({ level: 1 });

db.sources.createIndex({ id: 1 }, { unique: true });
db.sources.createIndex({ type: 1 });
db.sources.createIndex({ level: 1 });
db.sources.createIndex({ effectiveDate: 1 });
db.sources.createIndex({ amendmentDate: 1 }); 