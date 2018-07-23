[1mdiff --git a/src/PayFineUI.java b/src/PayFineUI.java[m
[1mindex 91cb9f3..6532154 100644[m
[1m--- a/src/PayFineUI.java[m
[1m+++ b/src/PayFineUI.java[m
[36m@@ -32,13 +32,13 @@[m [mpublic class PayFineUI {[m
 			switch (state) {[m
 			[m
 			case READY:[m
[31m-				String memStr = input("Swipe member card (press <enter> to cancel): ");[m
[31m-				if (memStr.length() == 0) {[m
[32m+[m				[32mString memberCardId = input("Swipe member card (press <enter> to cancel): ");[m
[32m+[m				[32mif (memberCardId.length() == 0) {[m
 					control.cancel();[m
 					break;[m
 				}[m
 				try {[m
[31m-					int memberId = Integer.valueOf(memStr).intValue();[m
[32m+[m					[32mint memberId = Integer.valueOf(memberCardId).intValue();[m
 					control.cardSwiped(memberId);[m
 				}[m
 				catch (NumberFormatException e) {[m
