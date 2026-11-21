# Admin Dashboard - Food Delivery Platform

Admin panel frontend for the Food Delivery Platform, built with React 18, TypeScript, Vite, and Tailwind CSS.

## Tech Stack

- **React 18** with TypeScript (strict mode)
- **Vite 5** for fast development and optimized builds
- **Tailwind CSS** for utility-first styling
- **React Router v6** with protected routes and role-based access control
- **Axios** with JWT auto-refresh interceptors and request queuing
- **Lucide React** for icons
- **date-fns** for date formatting

## Features

- **Admin-only JWT authentication** with automatic token refresh
- **8 Admin Pages**:
  - Dashboard - Overview statistics and recent activity
  - Users - Manage platform users with role/status updates
  - Restaurant Applications - Review and approve/reject restaurant registrations
  - Restaurants - Manage restaurant listings and statuses
  - Orders - View all platform orders with filtering
  - Deliveries - Track delivery status across the platform
  - Payments - Monitor payment transactions
  - Flagged Reviews - Moderate flagged customer reviews
  - System Configuration - Manage platform-wide settings
- **Responsive design** with collapsible sidebar
- **Dark mode support** with system preference detection
- **Accessible UI components** (modals, tables, forms, badges)
- **Server-side pagination, sorting, and filtering**

## Getting Started

### Prerequisites

- Node.js 18+
- Backend API running on `http://localhost:8080`

### Installation

```bash
npm install
```

### Development

```bash
npm run dev
```

The app will be available at `http://localhost:3001`

### Build

```bash
npm run build
```

### Type Check

```bash
npm run type-check
```

### Lint

```bash
npm run lint
```

## Project Structure

```
src/
├── components/
│   ├── layout/          # Layout components (Sidebar, TopNav, Layout)
│   └── ui/              # Reusable UI components (Button, Input, Card, Table, Modal, Badge)
├── context/
│   └── AuthContext.tsx  # Authentication state management
├── hooks/               # Custom React hooks
├── lib/
│   └── api.ts           # Axios client with interceptors
├── pages/               # Page components (8 admin pages)
├── types/
│   └── api.ts           # TypeScript types matching backend DTOs
├── App.tsx              # Routing configuration
├── main.tsx             # Application entry point
└── index.css            # Global styles with Tailwind
```

## API Integration

The frontend integrates with the Spring Boot backend admin endpoints:

- **Base URL**: `/api/v1/admin`
- **Authentication**: JWT Bearer tokens with automatic refresh
- **Endpoints**:
  - `GET /dashboard` - Dashboard statistics
  - `GET /users` - Paginated user list with filters
  - `PUT /users/{id}/status` - Update user status
  - `PUT /users/{id}/role` - Update user role
  - `GET /restaurant-applications` - Paginated applications
  - `POST /restaurant-applications/{id}/approve` - Approve application
  - `POST /restaurant-applications/{id}/reject` - Reject application
  - `GET /restaurants` - Paginated restaurant list
  - `PUT /restaurants/{id}/status` - Update restaurant status
  - `GET /orders` - Paginated order list
  - `GET /deliveries` - Paginated delivery list
  - `GET /payments` - Paginated payment list
  - `GET /reviews/flagged` - Paginated flagged reviews
  - `GET /config` - System configuration
  - `PUT /config/{key}` - Update configuration

## Authentication Flow

1. Admin logs in with email/password at `/login`
2. Backend returns access token (15min) + refresh token (7 days)
3. Access token stored in localStorage, sent with each request
4. On 401 response, refresh token automatically used to get new access token
5. Request queue prevents race conditions during concurrent 401s
6. On refresh failure, user redirected to login

## Design System

- **Colors**: Primary (blue), Neutral grays, Semantic colors (success, warning, danger, info)
- **Spacing**: 4px base unit
- **Typography**: System font stack
- **Components**: Consistent border radius (8px), shadows, transitions
- **Dark Mode**: CSS custom properties with `dark:` Tailwind variant

## Role-Based Access

- Only users with `ADMIN` role can access the dashboard
- Protected routes redirect to `/login` if not authenticated
- Public routes redirect to `/` if already authenticated as admin

## License

Private - Food Delivery Platform