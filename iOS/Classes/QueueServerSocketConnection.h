//
//  QueueServerSocketConnection.h
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/31/11.
//  Copyright 2011 SoftExcel Technologies Inc.,. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CFNetwork/CFSocketStream.h>
#import "TokenListener.h"

@interface QueueServerSocketConnection : NSObject {
	id<TokenListener> listener;
	
	// Host and Port to connect to...
	NSString* host;
	int port;
		
	// Stream to read 
	CFReadStreamRef inputDataStream;
	bool inputDataStreamOpen;
	NSMutableData* inputDataBuffer;	
}

@property(nonatomic,assign) id<TokenListener> listener;

// Initialize
- (id)init:(NSString*)host andPort:(int)port;

// Connect to the server
- (BOOL)connect;

// Close connection
- (void)disconnect;


@end
