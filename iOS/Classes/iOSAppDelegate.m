//
//  iOSAppDelegate.m
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/28/11.
//  Copyright SoftExcel Technologies Inc., 2011. All rights reserved.
//

#import "iOSAppDelegate.h"

@implementation iOSAppDelegate

@synthesize window;


- (void)applicationDidFinishLaunching:(UIApplication *)application {    

    // Override point for customization after application launch
    [window makeKeyAndVisible];
}


- (void)dealloc {
    [window release];
    [super dealloc];
}


@end
