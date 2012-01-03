//
//  Controller.m
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/30/11.
//  Copyright 2011 SoftExcel Technologies Inc.,. All rights reserved.
//

#import "Controller.h"
#import "SBJson.h"
#import "QueueServerSocketConnection.h"

@interface Controller() 
	
	@property(nonatomic,retain) QueueServerSocketConnection* connection;

@end


@implementation Controller

@synthesize connection;

// Called after all the GUI items have been instantiated
- (void) awakeFromNib{
	NSString *jsonString = [NSString stringWithString:@"{\"foo\": \"bar\"}"];
	NSDictionary *dictionary = [jsonString JSONValue];
	NSLog(@"Dictionary value for \"foo\" is \"%@\"", [dictionary objectForKey:@"foo"]);
	
	connection = [[QueueServerSocketConnection alloc] init:@"ec2-184-72-133-78.compute-1.amazonaws.com" andPort:7201];
	connection.listener = self;
	[connection connect];
}

// Cleanup
- (void)dealloc {
	self.connection = nil;
	[super dealloc];
}

- (void)connectionAttemptFailed:(QueueServerSocketConnection*)connection {
	nextAvailableToken.text = @"Socket Connection Failed";
}


- (void)connectionTerminated:(QueueServerSocketConnection*)connection {
	nextAvailableToken.text = @"Socket Connection Terminated";
}

- (void) receiveNewToken:(int)token {
	
	nextAvailableToken.text = [NSString stringWithFormat:@"%i", token];	
}

- (IBAction)getNextToken:sender {
	int num = arc4random() % 100;
	myTokenNumber.text = [NSString stringWithFormat:@"%i", num];
}
@end
