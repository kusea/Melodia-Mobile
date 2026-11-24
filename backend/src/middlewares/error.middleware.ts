import { Request, Response, NextFunction } from 'express';

export class AppError extends Error {
  statusCode: number;
  code: string;
  details: any[];

  constructor(statusCode: number, code: string, message: string, details: any[] = []) {
    super(message);
    this.statusCode = statusCode;
    this.code = code;
    this.details = details;
  }
}

export function requestIdMiddleware(req: Request, res: Response, next: NextFunction): void {
  req.requestId = `req_${Math.random().toString(36).substring(2, 9)}_${Date.now().toString(36)}`;
  res.setHeader('X-Request-Id', req.requestId);
  next();
}

export function errorMiddleware(err: any, req: Request, res: Response, next: NextFunction): void {
  const statusCode = err.statusCode || (err.status ? err.status : 500);
  const code = err.code || (statusCode === 500 ? 'INTERNAL_SERVER_ERROR' : 'BAD_REQUEST');
  const message = err.message || 'An unexpected internal server error occurred.';
  const details = err.details || [];
  const requestId = req.requestId || `req_${Date.now().toString(36)}`;

  if (statusCode === 500) {
    console.error(`[${requestId}] Error:`, err);
  }

  res.status(statusCode).json({
    error: {
      code,
      message,
      details,
      requestId,
    },
  });
}
