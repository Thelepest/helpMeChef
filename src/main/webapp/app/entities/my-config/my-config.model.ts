export interface IMyConfig {
  id?: number;
  mcKey?: string;
  mcValue?: string;
}

export class MyConfig implements IMyConfig {
  constructor(public id?: number, public mcKey?: string, public mcValue?: string) {}
}

export function getMyConfigIdentifier(myConfig: IMyConfig): number | undefined {
  return myConfig.id;
}
