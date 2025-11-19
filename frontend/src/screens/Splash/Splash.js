import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View, Image } from 'react-native';

const Logo = require('../../../assets/logo.png');

const Splash = ({navigatio}) => {

    useEffect( () => {
        setTimeout(() => {
            navigation.replace('Menu');
        }, 3000)
    },[])
    return(
        <View style={StyleSheet.container}>
            <Image style={styles.logo} source={Logo}/>
            <Text>Carregando...</Text>
        </View>
    );
};

const styles = StyleSheet.create({
    container:{
        flex:1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: "#ffffff"
    },
    title: {
        textAlign:'center',
        fontSize: '16',
        fontWeight: 'bold'
    },
    logo: {
        width:'70%',
        height:'70%',
        resizeMode:'contain'
    }
})
export default Splash;