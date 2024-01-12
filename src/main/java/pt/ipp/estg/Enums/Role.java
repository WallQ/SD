package pt.ipp.estg.Enums;

/**
 * The {@code Role} enum represents the roles that users can have in the application.
 * Each role signifies a different level of access or authority within the system.
 * Possible roles include Private, Sergeant, Lieutenant, and General.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public enum Role {
    /**
     * Represents the lowest level of access or authority.
     */
    Private,
    /**
     * Represents a role with higher access or authority than Private.
     */
    Sergeant,
    /**
     * Represents a role with higher access or authority than Sergeant.
     */
    Lieutenant,
    /**
     * Represents the highest level of access or authority.
     */
    General
}