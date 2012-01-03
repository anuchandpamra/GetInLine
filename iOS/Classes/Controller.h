//
//  Controller.h
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/30/11.
//  Copyright 2011 SoftExcel Technologies Inc.,. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QueueServerSocketConnection.h"

@interface Controller : NSObject <TokenListener> {
	IBOutlet UILabel *nextAvailableToken; // Label to show the next available token number
	IBOutlet UILabel *myTokenNumber; // Label to show the our latest token number
	QueueServerSocketConnection* connection; // Socket Connect

}

- (IBAction)getNextToken:sender;

@end
