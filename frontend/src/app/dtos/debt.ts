import {Category} from "./category";
import {GroupDto} from "./group";

export interface DebtGroupDetailDto {
  userEmail: string;
  groupId: number;
  membersDebts: { [key: string]: number };
}
