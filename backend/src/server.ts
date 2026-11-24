import { config } from './config/index.js';
import { createApp } from './app.js';
import { initDatabase } from './database/index.js';

async function bootstrap() {
  console.log('----------------------------------------------------');
  console.log('🎵 Melodia Lossless Music Streaming Backend Server');
  console.log('   HCMUT CO3043 Mobile Development - Group 14 (CC01)');
  console.log('----------------------------------------------------');

  // Initialize DB (Postgres or In-Memory fallback)
  await initDatabase();

  const app = createApp();

  const server = app.listen(config.port, '0.0.0.0', () => {
    console.log(`🚀 Server listening on port ${config.port}`);
    console.log(`📍 Local URL:     http://localhost:${config.port}/v1`);
    console.log(`📱 Emulator URL:  http://10.0.2.2:${config.port}/v1`);
    console.log(`📖 Swagger Docs:  http://localhost:${config.port}/v1/docs`);
    console.log(`🩺 Health Check:  http://localhost:${config.port}/health`);
    console.log('----------------------------------------------------');
  });

  // Graceful shutdown handling
  const shutdown = () => {
    console.log('\n🛑 Gracefully shutting down Melodia backend...');
    server.close(() => {
      console.log('💤 HTTP server closed. Goodbye!');
      process.exit(0);
    });
  };

  process.on('SIGTERM', shutdown);
  process.on('SIGINT', shutdown);
}

bootstrap().catch((err) => {
  console.error('Fatal initialization error:', err);
  process.exit(1);
});
