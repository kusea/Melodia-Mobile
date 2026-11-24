import dotenv from 'dotenv';
dotenv.config();

export const config = {
  port: parseInt(process.env.PORT || '3000', 10),
  nodeEnv: process.env.NODE_ENV || 'development',
  jwt: {
    accessSecret: process.env.JWT_ACCESS_SECRET || 'melodia_super_secret_access_jwt_key_2026',
    refreshSecret: process.env.JWT_REFRESH_SECRET || 'melodia_super_secret_refresh_jwt_key_2026',
    accessExpiresIn: parseInt(process.env.JWT_ACCESS_EXPIRES_IN || '900', 10), // 15 mins (NFR-06)
    refreshExpiresIn: parseInt(process.env.JWT_REFRESH_EXPIRES_IN || '604800', 10), // 7 days
  },
  database: {
    url: process.env.DATABASE_URL || 'postgresql://melodia:melodia_secret@localhost:5432/melodia_db',
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT || '5432', 10),
    user: process.env.DB_USER || 'melodia',
    password: process.env.DB_PASSWORD || 'melodia_secret',
    name: process.env.DB_NAME || 'melodia_db',
  },
  cdnBaseUrl: process.env.CDN_BASE_URL || 'https://cdn.melodia.example',
};
