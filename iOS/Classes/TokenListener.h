//
//  TokenListener.h
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/31/11.
//  Copyright 2011 SoftExcel Technologies Inc.,. All rights reserved.
//

@class QueueServerSocketConnection;

@protocol TokenListener
- (void) connectionAttemptFailed:(QueueServerSocketConnection*)connection;
- (void) connectionTerminated:(QueueServerSocketConnection*)connection;
- (void) receiveNewToken:(int)token;

@end