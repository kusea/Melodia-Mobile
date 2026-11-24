import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { Pool } from 'pg';
import { config } from '../config/index.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function runInit() {
  console.log('🔄 Initializing PostgreSQL database with Schema and Seeds...');
  console.log(`🔌 Target: ${config.database.url}`);

  const pool = new Pool({
    connectionString: config.database.url,
  });

  try {
    const client = await pool.connect();
    console.log('✅ Connected to database.');

    const schemaPath = path.join(__dirname, 'schema.sql');
    const seedPath = path.join(__dirname, 'seed.sql');

    if (fs.existsSync(schemaPath)) {
      console.log('📄 Executing schema.sql (17 Entities)...');
      const schemaSql = fs.readFileSync(schemaPath, 'utf-8');
      await client.query(schemaSql);
      console.log('✅ Schema tables, enums, and indexes created.');
    }

    if (fs.existsSync(seedPath)) {
      console.log('🌱 Executing seed.sql (Lossless Catalog Data)...');
      const seedSql = fs.readFileSync(seedPath, 'utf-8');
      await client.query(seedSql);
      console.log('✅ Seed data inserted successfully.');
    }

    client.release();
    console.log('🎉 Database initialization complete!');
  } catch (err) {
    console.error('❌ Failed to initialize database:', err);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

runInit();
