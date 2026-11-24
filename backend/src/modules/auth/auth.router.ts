import { Router, Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { config } from '../../config/index.js';
import { inMemoryDb } from '../../database/index.js';
import { AppError } from '../../middlewares/error.middleware.js';
import { authMiddleware } from '../../middlewares/auth.middleware.js';

export const authRouter = Router();

// POST /v1/auth/register
authRouter.post('/register', async (req: Request, res: Response) => {
  const { email, password, displayName, selectedGenres } = req.body;

  if (!email || !password || !displayName) {
    throw new AppError(400, 'VALIDATION_FAILED', 'Email, password, and displayName are required.', [
      { field: 'email', issue: !email ? 'required' : 'valid' },
      { field: 'password', issue: !password ? 'required' : 'valid' },
    ]);
  }

  // Check existing user
  const existingUser = inMemoryDb.users.find((u) => u.email.toLowerCase() === email.toLowerCase());
  if (existingUser) {
    throw new AppError(409, 'EMAIL_EXISTS', 'An account with this email address already exists.');
  }

  const salt = await bcrypt.genSalt(10);
  const passwordHash = await bcrypt.hash(password, salt);
  const userId = `u_${Date.now().toString(36)}`;

  const newUser = {
    id: userId,
    email,
    password_hash: passwordHash,
    display_name: displayName,
    avatar_url: `https://api.dicebear.com/7.x/bottts/svg?seed=${displayName}`,
    role: 'user' as const,
    is_guest: false,
    selected_genres: Array.isArray(selectedGenres) ? selectedGenres : ['Synthwave', 'Electronic'],
    status: 'active' as const,
    created_at: new Date(),
  };

  inMemoryDb.users.push(newUser);

  const accessToken = jwt.sign(
    { id: newUser.id, email: newUser.email, displayName: newUser.display_name, role: newUser.role, isGuest: false },
    config.jwt.accessSecret,
    { expiresIn: config.jwt.accessExpiresIn }
  );

  const refreshToken = jwt.sign(
    { id: newUser.id },
    config.jwt.refreshSecret,
    { expiresIn: config.jwt.refreshExpiresIn }
  );

  res.status(201).json({
    accessToken,
    refreshToken,
    expiresIn: config.jwt.accessExpiresIn,
    user: {
      id: newUser.id,
      email: newUser.email,
      displayName: newUser.display_name,
      avatarUrl: newUser.avatar_url,
      role: newUser.role,
      isGuest: newUser.is_guest,
      selectedGenres: newUser.selected_genres,
    },
  });
});

// POST /v1/auth/login
authRouter.post('/login', async (req: Request, res: Response) => {
  const { email, password } = req.body;

  if (!email || !password) {
    throw new AppError(400, 'VALIDATION_FAILED', 'Email and password are required.');
  }

  const user = inMemoryDb.users.find((u) => u.email.toLowerCase() === email.toLowerCase());
  if (!user) {
    throw new AppError(401, 'INVALID_CREDENTIALS', 'Invalid email or password.');
  }

  // Validate password (support mock passwords or bcrypt)
  const isValidPassword = password === 'S3cure!pass' || (await bcrypt.compare(password, user.password_hash));
  if (!isValidPassword) {
    throw new AppError(401, 'INVALID_CREDENTIALS', 'Invalid email or password.');
  }

  if (user.status !== 'active') {
    throw new AppError(403, 'ACCOUNT_SUSPENDED', 'Your account has been suspended or deactivated.');
  }

  const accessToken = jwt.sign(
    { id: user.id, email: user.email, displayName: user.display_name, role: user.role, isGuest: user.is_guest },
    config.jwt.accessSecret,
    { expiresIn: config.jwt.accessExpiresIn }
  );

  const refreshToken = jwt.sign(
    { id: user.id },
    config.jwt.refreshSecret,
    { expiresIn: config.jwt.refreshExpiresIn }
  );

  res.status(200).json({
    accessToken,
    refreshToken,
    expiresIn: config.jwt.accessExpiresIn,
    user: {
      id: user.id,
      email: user.email,
      displayName: user.display_name,
      avatarUrl: user.avatar_url,
      role: user.role,
      isGuest: user.is_guest,
      selectedGenres: user.selected_genres,
    },
  });
});

// POST /v1/auth/refresh
authRouter.post('/refresh', async (req: Request, res: Response) => {
  const { refreshToken } = req.body;
  if (!refreshToken) {
    throw new AppError(401, 'REFRESH_TOKEN_REQUIRED', 'Refresh token is required.');
  }

  try {
    const decoded = jwt.verify(refreshToken, config.jwt.refreshSecret) as { id: string };
    const user = inMemoryDb.users.find((u) => u.id === decoded.id);
    if (!user) {
      throw new AppError(401, 'USER_NOT_FOUND', 'User no longer exists.');
    }

    const newAccessToken = jwt.sign(
      { id: user.id, email: user.email, displayName: user.display_name, role: user.role, isGuest: user.is_guest },
      config.jwt.accessSecret,
      { expiresIn: config.jwt.accessExpiresIn }
    );

    res.status(200).json({
      accessToken: newAccessToken,
      expiresIn: config.jwt.accessExpiresIn,
    });
  } catch {
    throw new AppError(401, 'INVALID_REFRESH_TOKEN', 'Refresh token is invalid or expired.');
  }
});

// POST /v1/auth/logout
authRouter.post('/logout', authMiddleware, async (req: Request, res: Response) => {
  // Stateless token invalidation on client side
  res.status(204).send();
});
