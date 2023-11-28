// import javax.swing.JOptionPane;

// public class LoginChecks {
    
//     private DatabaseConnector databaseConnector;

//     private LoginController loginController; 

//     public LoginChecks(DatabaseConnector databaseConnector) {
//         this.databaseConnector = databaseConnector;

//         loginController = new LoginController(databaseConnector);
//     }

//     private void checkMemberAttributes(UserType userType, String username) {
//         if (userType == UserType.Registered) {
//             // Check membership attributes and prompt if needed
//             boolean isMember = loginController.getMembershipStatus(username);
//             if (!isMember) {
//                 int option = JOptionPane.showConfirmDialog(this,
//                         "Would you like to become a member of Vortex Airlines Rewards Program?", "Membership",
//                         JOptionPane.YES_NO_OPTION);

//                 if (option == JOptionPane.YES_OPTION) {
//                     loginController.updateMembershipStatus(username, true);
//                 }
//             }
//         }
//     }

//     private void checkCreditCard(UserType userType, String username) {
//         // Check credit card status and prompt if needed
//         boolean hasCompanyCreditCard = loginController.getCompanyCreditCardStatus(username);
//         if (!hasCompanyCreditCard) {
//             int option = JOptionPane.showConfirmDialog(this,
//                     "Would you like to apply for a Vortex Airlines credit card?", "Credit Card",
//                     JOptionPane.YES_NO_OPTION);

//             if (option == JOptionPane.YES_OPTION) {
//                 UserController.updateCreditCardStatus(databaseConnector, username, true);
//             }
//         }
//     }

//     private void checkRedeemedCompanionTicket(UserType userType, String username) {
//         if (userType == UserType.Registered) {
//             // Check companion ticket status and prompt if needed
//             boolean hasRedeemedCompanionTicket = loginController.getCompanionTicketRedemptionStatus(username);
//             if (!hasRedeemedCompanionTicket) {
//                 int option = JOptionPane.showConfirmDialog(this,
//                         "Would you like to redeem your 1 free companion ticket?", "Companion Ticket",
//                         JOptionPane.YES_NO_OPTION);

//                 if (option == JOptionPane.YES_OPTION) {
//                     loginController.updateCompanionTicketRedemptionStatus(username, true);
//                 }
//             }
//         }
//     }
// }
