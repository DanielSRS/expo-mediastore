// Import the native module. On web, it will be resolved to ExpoMediastore.web.ts
// and on native platforms to ExpoMediastore.ts
import ExpoMediastoreModule from './ExpoMediastoreModule';

// Get the native constant value.
export const PI = ExpoMediastoreModule.PI;

export function hello(): string {
  return ExpoMediastoreModule.hello();
}
