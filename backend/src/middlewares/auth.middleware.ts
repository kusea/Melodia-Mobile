import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { config } from '../config/index.js';

export interface AuthenticatedUser {
  id: string;
  email: string;
  displayName: string;
  role: 'user' | 'admin' | 'moderator';
  isGuest: boolean;
}

declare global {
  namespace Express {
    interface Request {
      user?: AuthenticatedUser;
      requestId?: string;
    }
  }
}

export function authMiddleware(req: Request, res: Response, next: NextFunction): void {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    res.status(401).json({
      error: {
        code: 'UNAUTHORIZED',
        message: 'Missing, invalid or expired access token.',
        details: [],
        requestId: req.requestId || `req_${Date.now().toString(36)}`,
      },
    });
    return;
  }

  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, config.jwt.accessSecret) as AuthenticatedUser;
    req.user = decoded;
    next();
  } catch (err) {
    res.status(401).json({
      error: {
        code: 'TOKEN_EXPIRED',
        message: 'Session token has expired. Please refresh or log in again.',
        details: [],
        requestId: req.requestId || `req_${Date.now().toString(36)}`,
      },
    });
  }
}

export function optionalAuthMiddleware(req: Request, res: Response, next: NextFunction): void {
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.split(' ')[1];
    try {
      const decoded = jwt.verify(token, config.jwt.accessSecret) as AuthenticatedUser;
      req.user = decoded;
    } catch {
      // Ignored for optional auth; falls back to guest
    }
  }
  next();
}

export function requireAdmin(req: Request, res: Response, next: NextFunction): void {
  if (!req.user || req.user.role !== 'admin') {
    res.status(403).json({
      error: {
        code: 'FORBIDDEN',
        message: 'Administrator privileges required for this action.',
        details: [],
        requestId: req.requestId || `req_${Date.now().toString(36)}`,
      },
    });
    return;
  }
  next();
}
