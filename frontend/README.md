# IBM Bob Hackathon - Flight Dashboard Frontend

Angular 17 frontend application for the IBM Bob Auto-Fix demonstration.

## Features

- **Flight Dashboard**: Display 50 flights with pagination
- **Real-time Error Detection**: Highlights flights with intentional bugs
- **Auto-Fix UI**: Trigger Bob's auto-fix workflow with one click
- **PR Integration**: View generated GitHub pull requests
- **Test Recording**: Record Playwright tests for verification
- **System Status**: Real-time monitoring of backend services

## Prerequisites

- Node.js 18+ and npm
- Angular CLI 17

## Installation

```bash
npm install
```

## Development Server

```bash
npm start
# or
ng serve
```

Navigate to `http://localhost:4200/`

## Build

```bash
npm run build
```

Build artifacts will be stored in the `dist/` directory.

## Architecture

- **Angular 17**: Latest LTS version with standalone components support
- **Material UI**: Google's Material Design components
- **RxJS**: Reactive programming for async operations
- **TypeScript**: Strict mode enabled for type safety

## API Integration

The frontend connects to the Spring Boot backend at `http://localhost:8080`:

- `GET /api/flights`: Fetch paginated flight data
- `POST /api/auto-fix`: Trigger auto-fix workflow
- `POST /api/record-test`: Record Playwright test
- `GET /api/auto-fix/status`: Get system status

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── services/
│   │   │   ├── flight.service.ts
│   │   │   └── auto-fix.service.ts
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   └── app.module.ts
│   ├── assets/
│   ├── index.html
│   ├── main.ts
│   └── styles.scss
├── angular.json
├── package.json
└── tsconfig.json
```

## Intentional Bugs Demonstrated

The dashboard highlights flights with:
- **Null Airlines**: Triggers NullPointerException
- **Negative Delays**: Triggers IndexOutOfBoundsException
- **Zero Flight Count**: Triggers ArithmeticException
- **Invalid Date Formats**: Triggers DateTimeParseException

## Bob Auto-Fix Workflow

1. User clicks "Fix This Bug" on a problematic flight
2. Frontend sends error details to `/api/auto-fix`
3. Bob analyzes the error and generates a fix
4. Bob creates a GitHub PR with the fix
5. Bob sends Slack notification
6. Frontend displays PR link and fix snippet

## Testing

The application includes integration with Playwright for E2E testing:

```bash
npm run test:e2e
```

## License

IBM Hackathon Project - 2026